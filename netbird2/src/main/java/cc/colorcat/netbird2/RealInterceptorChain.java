package cc.colorcat.netbird2;

import java.io.IOException;
import java.util.List;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
final class RealInterceptorChain implements Interceptor.Chain {
    private final List<Interceptor> interceptors;
    private final int index;
    private final Request<?> request;
    private final Connection connection;

    RealInterceptorChain(List<Interceptor> interceptors, int index, Request<?> request, Connection connection) {
        this.interceptors = interceptors;
        this.index = index;
        this.request = request;
        this.connection = connection;
    }

    @Override
    public Connection connection() {
        return connection;
    }

    @Override
    public Request<?> request() {
        return request;
    }

    @Override
    public Response proceed(Request<?> request) throws IOException {
        RealInterceptorChain next = new RealInterceptorChain(interceptors, index + 1, request, connection);
        Interceptor interceptor = interceptors.get(index);
        return interceptor.intercept(next);
    }
}
