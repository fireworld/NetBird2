package cc.colorcat.netbird2;

import java.io.IOException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

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
    private NetBird netBird;
    private final Set<Call> running = new CopyOnWriteArraySet<>();
    private final Queue<Call> waiting = new ConcurrentLinkedQueue<>();

    public Dispatcher(NetBird netBird) {
        this.netBird = netBird;
    }

    public void execute(Call call) {
        if (!waiting.contains(call)) {
            if (waiting.offer(call)) notifyNewCall();
        }
    }

    private void notifyNewCall() {
        if (running.size() < netBird.maxRunning && !waiting.isEmpty()) {
            Call call = waiting.poll();
            if (running.add(call)) {
                netBird.executor.execute(new Task(call));
            }
        }
    }

    public void cancelWait(Object tag) {

    }

    public void cancelRunning(Object tag) {

    }

    private class Task implements Runnable {
        private Call call;

        private Task(Call call) {
            this.call = call;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            Request<?> request = call.request();
            try {
                Response response = call.execute();
                if (response.code() == 200 && response.body() != null) {
                    NetworkData data = request.parse(response);
                    request.deliver(data);
                } else {
                    NetworkData data = NetworkData.newFailure(response.code(), response.msg());
                    request.deliver(data);
                }
            } catch (IOException e) {
                LogUtils.e(e);
                NetworkData data = NetworkData.newFailure(Const.CODE_UNKNOWN, Utils.emptyElse(e.getMessage(), Const.MSG_UNKNOWN));
                request.deliver(data);
            } finally {
                running.remove(call);
                notifyNewCall();
            }
        }
    }
}
