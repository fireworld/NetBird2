package cc.colorcat.netbird2;

import java.io.IOException;

import cc.colorcat.netbird2.meta.Headers;
import cc.colorcat.netbird2.request.Request;
import cc.colorcat.netbird2.request.RequestBody;

/**
 * Created by cxx on 2017/2/24.
 * xx.ch@outlook.com
 */
final class RequestProcessInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request<?> request = chain.request();
        Request.Builder<?> builder = request.newBuilder();
        RequestBody body = request.body();
        if (body != null) {
            String contentType = body.contentType();
            if (contentType != null) {
                builder.setHeader(Headers.CONTENT_TYPE, contentType);
            }
            long contentLength = body.contentLength();
            if (contentLength != -1L) {
                builder.setHeader(Headers.CONTENT_LENGTH, Long.toString(contentLength));
                builder.removeHeader("Transfer-Encoding");
            } else {
                builder.setHeader("Transfer-Encoding", "chunked");
                builder.removeHeader(Headers.CONTENT_LENGTH);
            }
        }
        builder.addHeaderIfNot("Connection", "Keep-Alive");
        builder.addHeaderIfNot("User-Agent", Version.userAgent());
        return chain.proceed(builder.build());
    }
}
