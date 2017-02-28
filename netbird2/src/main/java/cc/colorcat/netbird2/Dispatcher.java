package cc.colorcat.netbird2;

import java.io.IOException;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cc.colorcat.netbird2.RealCall.AsyncCall;
import cc.colorcat.netbird2.util.LogUtils;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
public class Dispatcher {
    private static final String TAG = "DispatcherTAG";
    private final NetBird netBird;
    private final Queue<AsyncCall> waitingAsyncCalls = new ConcurrentLinkedQueue<>();
    private final Queue<AsyncCall> runningAsyncCalls = new ConcurrentLinkedQueue<>();
    private final Queue<RealCall> runningSyncCalls = new ConcurrentLinkedQueue<>();

    public Dispatcher(NetBird netBird) {
        this.netBird = netBird;
    }

    public boolean executed(RealCall call) {
        return !runningSyncCalls.contains(call) && runningSyncCalls.add(call);
    }

    public void enqueue(AsyncCall call) {
        if (!waitingAsyncCalls.contains(call) && waitingAsyncCalls.offer(call)) {
            promoteCalls();
        } else {
            call.callback().onFailure(call.get(), new IOException(Const.MSG_DUPLICATE_REQUEST));
        }
        logSize(2, "enqueue");
    }

    private synchronized void promoteCalls() {
        if (runningAsyncCalls.size() >= netBird.maxRunning()) return;

        while (!waitingAsyncCalls.isEmpty()) {
            AsyncCall call = waitingAsyncCalls.poll();
            if (!runningAsyncCalls.contains(call) && runningAsyncCalls.add(call)) {
                netBird.executor().execute(call);
                if (runningAsyncCalls.size() >= netBird.maxRunning()) return;
            } else {
                call.callback().onFailure(call.get(), new IOException(Const.MSG_DUPLICATE_REQUEST));
            }
        }
//        logSize(2, "promoteCalls");
    }

    public void cancelWaiting(Object tag) {
        Iterator<AsyncCall> iterator = waitingAsyncCalls.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().request().tag().equals(tag)) {
                iterator.remove();
            }
        }
    }

    public void cancelAll(Object tag) {
        cancelWaiting(tag);
        for (AsyncCall call : runningAsyncCalls) {
            if (call.request().tag().equals(tag)) {
                call.get().cancel();
            }
        }
        for (RealCall call : runningSyncCalls) {
            if (call.request().tag().equals(tag)) {
                call.cancel();
            }
        }
    }


    public void cancelAll() {
        Iterator<AsyncCall> iterator = waitingAsyncCalls.iterator();
        while (iterator.hasNext()) {
            iterator.remove();
        }
        for (AsyncCall call : runningAsyncCalls) {
            call.get().cancel();
        }
        for (RealCall call : runningSyncCalls) {
            call.cancel();
        }
    }

    void finished(RealCall call) {
        runningSyncCalls.remove(call);
        logSize(3, "finished Realcall");
    }

    void finished(AsyncCall call) {
        runningAsyncCalls.remove(call);
        promoteCalls();
        logSize(4, "finished AsyncCall");
    }

    private void logSize(int level, String mark) {
        String msg = mark + ": " + "waiting = " + waitingAsyncCalls.size() + ", running = " + runningAsyncCalls.size();
        switch (level) {
            case 1:
                LogUtils.v(TAG, msg);
                break;
            case 2:
                LogUtils.d(TAG, msg);
                break;
            case 3:
                LogUtils.i(TAG, msg);
                break;
            case 4:
                LogUtils.w(TAG, msg);
                break;
            case 5:
                LogUtils.e(TAG, msg);
                break;
            default:
                LogUtils.ii(TAG, msg);

        }
    }
}
