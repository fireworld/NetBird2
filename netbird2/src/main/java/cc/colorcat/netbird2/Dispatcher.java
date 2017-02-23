package cc.colorcat.netbird2;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

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

    }

    private void realExecute(Call call) {
//        Request<?> request = interceptRequest(call.request());
//        Response response = interceptResponse(call.execute());
    }

    public void cancelWait(Object tag) {

    }

    public void cancelRunning(Object tag) {

    }

//    private Request<?> interceptRequest(Request<?> request) {
//        Request<?> result = request;
//        for (Interceptor interceptor : netBird.interceptors) {
//            result = interceptor.intercept(result);
//        }
//        return result;
//    }
//
//    private Response interceptResponse(Response response) {
//        Response result = response;
//        for (Interceptor interceptor : netBird.interceptors) {
//            result = interceptor.intercept(result);
//        }
//        return response;
//    }
}
