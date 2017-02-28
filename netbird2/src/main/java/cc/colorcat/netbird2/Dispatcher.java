package cc.colorcat.netbird2;

import java.io.IOException;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    private static final NetworkData DATA_WAITING;
    private static final NetworkData DATA_EXECUTING;

    static {
        DATA_WAITING = NetworkData.newFailure(Const.CODE_WAITING, Const.MSG_WAITING);
        DATA_EXECUTING = NetworkData.newFailure(Const.CODE_EXECUTING, Const.MSG_EXECUTING);
    }

    private final NetBird netBird;
    private final Queue<Call> running = new ConcurrentLinkedQueue<>();
    private final Queue<Call> waiting = new ConcurrentLinkedQueue<>();

    public Dispatcher(NetBird netBird) {
        this.netBird = netBird;
    }

    @SuppressWarnings("unchecked")
    public void execute(Call call) {
        if (!waiting.contains(call) && waiting.offer(call)) {
            notifyNewCall();
        } else {
            call.request().deliver(DATA_WAITING);
        }
    }

    @SuppressWarnings("unchecked")
    private void notifyNewCall() {
        if (running.size() < netBird.maxRunning() && !waiting.isEmpty()) {
            Call call = waiting.poll();
            if (!running.contains(call) && running.add(call)) {
                netBird.executor().execute(new Task(Dispatcher.this, call));
            } else {
                call.request().deliver(DATA_EXECUTING);
            }
        }
    }

    public void cancelAll(Object tag) {
        cancelWait(tag);
        cancelRunning(tag);
    }

    public void cancelWait(Object tag) {
        Iterator<Call> iterator = waiting.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().request().tag().equals(tag)) {
                iterator.remove();
            }
        }
    }

    public void cancelRunning(Object tag) {
        for (Call call : running) {
            if (call.request().tag().equals(tag)) {
                call.cancel();
            }
        }
    }

    private void finished(Call call) {
        running.remove(call);
    }

    private static class Task implements Runnable {
        private Dispatcher dispatcher;
        private Call call;

        private Task(Dispatcher dispatcher, Call call) {
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
                dispatcher.notifyNewCall();
                Utils.close(call);
            }
            if (data == null) {
                data = NetworkData.newFailure(code, msg);
            }
            request.deliver(data);
        }
    }
}
