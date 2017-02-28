package cc.colorcat.netbird2;

import java.io.IOException;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cc.colorcat.netbird2.RealCall.AsyncCall;
import cc.colorcat.netbird2.request.Request;
import cc.colorcat.netbird2.response.NetworkData;
import cc.colorcat.netbird2.response.Response;
import cc.colorcat.netbird2.util.LogUtils;
import cc.colorcat.netbird2.util.Utils;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
public class Dispatcher {
    private final NetBird netBird;
    private final Queue<AsyncCall> waitingAsyncCalls = new ConcurrentLinkedQueue<>();
    private final Queue<AsyncCall> runningAsyncCalls = new ConcurrentLinkedQueue<>();
    private final Queue<RealCall> runningSyncCalls = new ConcurrentLinkedQueue<>();

    public Dispatcher(NetBird netBird) {
        this.netBird = netBird;
    }

    @SuppressWarnings("unchecked")
    public boolean executed(RealCall call) {
        return !runningSyncCalls.contains(call) && runningSyncCalls.add(call);
//        if (!waiting.contains(call) && waiting.offer(call)) {
//            promoteCalls();
//        } else {
//            call.request().deliver(DATA_WAITING);
//        }
    }

    public void enqueue(AsyncCall call) {
        if (!waitingAsyncCalls.contains(call) && waitingAsyncCalls.offer(call)) {
            promoteCalls();
        } else {
            responseAsyncCall(call, RealCall.RESPONSE_WAITING);
        }
    }

    private void promoteCalls() {
        if (runningAsyncCalls.size() >= netBird.maxRunning()) return;
        if (waitingAsyncCalls.isEmpty()) return;

        while (!waitingAsyncCalls.isEmpty()) {
            AsyncCall call = waitingAsyncCalls.poll();
            if (!runningAsyncCalls.contains(call) && runningAsyncCalls.add(call)) {
                netBird.executor().execute(call);
                if (runningAsyncCalls.size() >= netBird.maxRunning()) return;
            } else {
                responseAsyncCall(call, RealCall.RESPONSE_EXECUTING);
            }
        }
    }

    private void responseAsyncCall(AsyncCall call, Response response) {
        Callback callback = call.callback();
        if (callback != null) {
            callback.onResponse(call.get(), response);
        }
    }

    public void cancelAll(Object tag) {
        cancelWait(tag);
        cancelRunning(tag);
    }

    public void cancelWait(Object tag) {
        Iterator<AsyncCall> iterator = waitingAsyncCalls.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().request().tag().equals(tag)) {
                iterator.remove();
            }
        }
    }

    public void cancelRunning(Object tag) {
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
    }

    void finished(AsyncCall call) {
        runningAsyncCalls.remove(call);
        promoteCalls();
    }

    private static class Task implements Runnable {
        private Dispatcher dispatcher;
        private RealCall call;

        private Task(Dispatcher dispatcher, RealCall call) {
            this.dispatcher = dispatcher;
            this.call = call;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            Request<?> request = call.request();
            NetworkData data = null;
            int code = Const.CODE_CONNECT_ERROR;
            String msg = Const.MSG_CONNECT_ERROR;
            try {
                Response response = call.execute();
                code = response.code();
                msg = Utils.nullElse(response.msg(), msg);
                if (code == 200 && response.body() != null) {
                    data = request.parse(response);
                }
            } catch (IOException e) {
                LogUtils.e(e);
                msg = Utils.formatMsg(msg, e);
            } finally {
                dispatcher.finished(call);
                dispatcher.promoteCalls();
                Utils.close(call);
            }
            if (data == null) {
                data = NetworkData.newFailure(code, msg);
            }
            request.deliver(data);
        }
    }
}
