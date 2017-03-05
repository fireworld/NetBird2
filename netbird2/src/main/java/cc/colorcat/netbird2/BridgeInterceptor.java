package cc.colorcat.netbird2;

import java.io.IOException;

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
        String url = Utils.nullElse(request.url(), baseUrl);
        String path = request.path();
        if (path != null) url += path;

        Method method = request.method();
        if (method == Method.GET) {
            String parameters = concatParameters(request.parameters());
            if (parameters != null) {
                url = url + '?' + parameters;
            }
        }
        Request.Builder builder = request.newBuilder().url(url).path(null);

        if (method == Method.POST) {
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
        }
        builder.addHeaderIfNot("Connection", "Keep-Alive");
        builder.addHeaderIfNot("User-Agent", Version.userAgent());
        return chain.proceed(builder.build().freeze());
    }

    private static String concatParameters(Parameters parameters) {
        if (parameters.isEmpty()) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = parameters.size(); i < size; i++) {
            if (i > 0) sb.append('&');
            String encodedName = Utils.smartEncode(parameters.name(i));
            String encodedValue = Utils.smartEncode(parameters.value(i));
            sb.append(encodedName).append('=').append(encodedValue);
        }
        return sb.toString();
    }
}
