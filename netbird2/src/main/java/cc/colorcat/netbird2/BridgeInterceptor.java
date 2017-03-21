package cc.colorcat.netbird2;

import java.io.IOException;
import java.util.List;

/**
 * Created by cxx on 2017/2/24.
 * xx.ch@outlook.com
 */
final class BridgeInterceptor implements Interceptor {
    private final String baseUrl;

    BridgeInterceptor(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        String url = Utils.nullElse(builder.url(), baseUrl);
        String path = builder.path();
        if (path != null) url += path;

        RequestBody body;
        if (!builder.method().needBody()) {
            String parameters = concatParameters(builder.names(), builder.values());
            if (parameters != null) {
                url = url + '?' + parameters;
            }
        } else if ((body = request.body()) != null) {
            String contentType = body.contentType();
            if (contentType != null) {
                builder.setHeader(Headers.CONTENT_TYPE, contentType);
            }
            long contentLength = body.contentLength();
            if (contentLength != -1L) {
                builder.setHeader(Headers.CONTENT_LENGTH, Long.toString(contentLength))
                        .removeHeader("Transfer-Encoding");
            } else {
                builder.setHeader("Transfer-Encoding", "chunked")
                        .removeHeader(Headers.CONTENT_LENGTH);
            }
        }
        builder.url(url).path(null)
                .addHeaderIfNot("Connection", "Keep-Alive")
                .addHeaderIfNot("User-Agent", Version.userAgent());
        return chain.proceed(builder.build().freeze());
    }

    private static String concatParameters(List<String> names, List<String> values) {
        if (names.isEmpty()) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = names.size(); i < size; ++i) {
            if (i > 0) sb.append('&');
            String encodedName = Utils.smartEncode(names.get(i));
            String encodedValue = Utils.smartEncode(values.get(i));
            sb.append(encodedName).append('=').append(encodedValue);
        }
        return sb.toString();
    }
}
