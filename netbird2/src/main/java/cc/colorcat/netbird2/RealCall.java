package cc.colorcat.netbird2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.colorcat.netbird2.request.Request;
import cc.colorcat.netbird2.response.Response;
import cc.colorcat.netbird2.util.Utils;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
public final class RealCall implements Call {
    private final NetBird netBird;
    private final Request<?> request;
    private final Connection connection;
    private final Interceptor requestProcess;
    private final AtomicBoolean executed;

    public RealCall(NetBird netBird, Request<?> originalRequest) {
        this.netBird = netBird;
        this.request = originalRequest;
        this.connection = netBird.connection().clone();
        this.requestProcess = new RequestProcessInterceptor(netBird);
        this.executed = new AtomicBoolean(false);
    }

    @Override
    public Request<?> request() {
        return request;
    }

    @Override
    public Response execute() throws IOException {
        if (executed.getAndSet(true)) throw new IllegalStateException("Already Executed");
        try {
            if (netBird.dispatcher().executed(this)) {
                return getResponseWithInterceptorChain();
            }
            throw new IOException(Const.MSG_DUPLICATE_REQUEST);
        } finally {
            netBird.dispatcher().finished(this);
            Utils.close(connection);
        }
    }

    @Override
    public void enqueue(Callback callback) {
        if (executed.getAndSet(true)) throw new IllegalStateException("Already Executed");
        request.onStart();
        netBird.dispatcher().enqueue(new AsyncCall(callback));
    }

    private Response getResponseWithInterceptorChain() throws IOException {
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
                ", executed=" + executed +
                '}';
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    protected RealCall clone() {
        return new RealCall(netBird, request);
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
                Response response = getResponseWithInterceptorChain();
                callback.onResponse(RealCall.this, response);
            } catch (IOException e) {
                callback.onFailure(RealCall.this, e);
            } finally {
                netBird.dispatcher().finished(this);
                Utils.close(RealCall.this.connection);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AsyncCall asyncCall = (AsyncCall) o;

            return RealCall.this.request.equals(asyncCall.request());

        }

        @Override
        public int hashCode() {
            return 31 * RealCall.this.request.hashCode();
        }
    }
}
