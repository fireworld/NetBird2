package cc.colorcat.netbird2;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public class Dispatcher {
    private NetBird netBird;

    public Dispatcher(NetBird netBird) {
        this.netBird = netBird;
    }

    public void execute(Call call) {

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
