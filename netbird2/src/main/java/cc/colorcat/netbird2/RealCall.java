package cc.colorcat.netbird2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.colorcat.netbird2.request.Request;
import cc.colorcat.netbird2.response.Response;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
public final class RealCall implements Call {
    static final Response RESPONSE_WAITING;
    static final Response RESPONSE_EXECUTING;

    static {
        RESPONSE_WAITING = new Response.Builder()
                .code(Const.CODE_WAITING)
                .msg(Const.MSG_WAITING)
                .build();

        RESPONSE_EXECUTING = new Response.Builder()
                .code(Const.CODE_EXECUTING)
                .msg(Const.MSG_EXECUTING)
                .build();
    }

    private final NetBird netBird;
    private final Request<?> request;
    private final Connection connection;
    private final Interceptor requestProcess;

    public RealCall(NetBird netBird, Request<?> originalRequest) {
        this.netBird = netBird;
        this.request = originalRequest;
        this.connection = netBird.connection().clone();
        this.requestProcess = new RequestProcessInterceptor(netBird);
    }

    @Override
    public Request<?> request() {
        return request;
    }

    @Override
    public void enqueue(Callback callback) {
        netBird.dispatcher().enqueue(new AsyncCall(callback));
    }

    @Override
    public Response execute() throws IOException {
        Response response;
        try {
            if (netBird.dispatcher().executed(this)) {
                response = getResponseWithInterceptorChain(false);
            } else {
                response = RESPONSE_EXECUTING;
            }
        } finally {
            netBird.dispatcher().finished(this);
        }
        return response;
    }

    private Response getResponseWithInterceptorChain(boolean asyncCall) throws IOException {
        if (asyncCall) {
            request.onStart();
        }
        List<Interceptor> head = netBird.headInterceptors();
        List<Interceptor> tail = netBird.tailInterceptors();
        int size = head.size() + tail.size();
        List<Interceptor> interceptors = new ArrayList<>(size + 2);
        interceptors.addAll(head);
        interceptors.add(requestProcess);
        interceptors.addAll(tail);
        interceptors.add(new ConnectInterceptor(netBird));
        Interceptor.Chain chain = new RealInterceptorChain(interceptors, 0, request, connection);
        return chain.proceed(request);
    }

    @Override
    public void cancel() {
        connection.cancel();
    }

    @Override
    public void close() throws IOException {
        connection.close();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RealCall)) return false;

        RealCall realCall = (RealCall) o;

        return request.equals(realCall.request);
    }

    @Override
    public int hashCode() {
        return request.hashCode();
    }

    @Override
    public String toString() {
        return "RealCall{" +
                "request=" + request +
                '}';
    }

    final class AsyncCall implements Runnable {
        private final Callback callback;

        AsyncCall(Callback callback) {
            this.callback = callback;
        }

        Request<?> request() {
            return RealCall.this.request;
        }

        RealCall get() {
            return RealCall.this;
        }

        Callback callback() {
            return callback;
        }

        @Override
        public void run() {
            try {
                Response response = getResponseWithInterceptorChain(true);
                if (callback != null) {
                    callback.onResponse(RealCall.this, response);
                }
            } catch (IOException e) {
                if (callback != null) {
                    callback.onFailure(RealCall.this, e);
                }
            } finally {
                netBird.dispatcher().finished(this);
            }
//            Request<?> request = call.request();
//            NetworkData data = null;
//            int code = Const.CODE_CONNECT_ERROR;
//            String msg = Const.MSG_CONNECT_ERROR;
//            try {
//                Response response = call.execute();
//                code = response.code();
//                msg = Utils.nullElse(response.msg(), msg);
//                if (code == 200 && response.body() != null) {
//                    data = request.parse(response);
//                }
//            } catch (IOException e) {
//                LogUtils.e(e);
//                msg = Utils.formatMsg(msg, e);
//            } finally {
//                dispatcher.finished(call);
//                dispatcher.promoteCalls();
//                Utils.close(call);
//            }
//            if (data == null) {
//                data = NetworkData.newFailure(code, msg);
//            }
//            request.deliver(data);
        }
    }
}
