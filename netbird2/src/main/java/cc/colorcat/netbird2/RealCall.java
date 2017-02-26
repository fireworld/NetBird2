package cc.colorcat.netbird2;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.colorcat.netbird2.request.Request;
import cc.colorcat.netbird2.response.Response;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
public class RealCall implements Call, Comparable<RealCall> {
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
    public void enqueue() {
        netBird.dispatcher().execute(this);
    }

    @Override
    public Response execute() throws IOException {
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

    @Override
    public int compareTo(@NonNull RealCall o) {
        return request.compareTo(o.request);
    }
}
