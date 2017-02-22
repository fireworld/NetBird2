package cc.colorcat.netbird2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public class RealCall implements Call {
    private Request<?> request;
    private NetBird netBird;
    private Connection connection;

    public RealCall(NetBird netBird, Request<?> originalRequest) {
        this.netBird = netBird;
        this.request = originalRequest;
    }

    @Override
    public Request<?> request() {
        return request;
    }

    @Override
    public void enqueue() {
        netBird.dispatcher.execute(this);
    }

    @Override
    public Response execute() throws IOException {
        List<Interceptor> interceptors = new ArrayList<>(netBird.interceptors);
        interceptors.add(new ConnectInterceptor(netBird));
        Interceptor.Chain chain = new RealInterceptorChain(interceptors, 0, request);
        return chain.proceed(request);
    }

//    private Response test() {
//        Request<?> req = interceptRequest(request);
//        connection = netBird.connection.clone();
//        Headers headers = null;
//        ResponseBody body = null;
//        int code = Const.CODE_CONNECT_ERROR;
//        String msg = Const.MSG_CONNECT_ERROR;
//        try {
//            connection.connect(netBird, req);
//            connection.writeHeaders(req.headers());
//            if (req.method() == Request.Method.POST) {
//                connection.writeBody(req.body());
//                connection.flush();
//            }
//            code = connection.responseCode();
//            msg = connection.responseMsg();
//            if (code == 200) {
//                headers = connection.responseHeaders();
//                body = connection.responseBody(headers);
//            }
//        } catch (IOException e) {
//            msg = Utils.formatMsg(msg, e);
//        }
//        Response response = new Response.Builder().code(code).msg(msg).headers(headers).body(body).build();
//        return interceptResponse(response);
//    }

    @Override
    public void cancel() {
        if (connection != null) {
            connection.cancel();
        }
    }

    @Override
    public void close() throws IOException {
        if (connection != null) {
            connection.close();
        }
    }

//    private Request<?> interceptRequest(Request<?> request) {
////        Request<?> result = request;
////        for (Interceptor interceptor : netBird.interceptors) {
////            result = interceptor.intercept(result);
////        }
//        return request;
//    }
//
//    private Response interceptResponse(Response response) {
////        Response result = response;
////        for (Interceptor interceptor : netBird.interceptors) {
////            result = interceptor.intercept(result);
////        }
//        return response;
//    }
}
