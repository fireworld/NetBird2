package cc.colorcat.netbird2;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Created by cxx on 17-4-7.
 * xx.ch@outlook.com
 */
final class GzipInterceptor implements Interceptor {
    private final boolean enabledGzip;

    GzipInterceptor(boolean enabledGzip) {
        this.enabledGzip = enabledGzip;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!enabledGzip) return chain.proceed(chain.request());
        boolean transparentGzip = false;
        Request.Builder builder = chain.request().unfreeze().newBuilder();
        if (builder.headerValue("Accept-Encoding") == null && builder.headerValue("Range") == null) {
            transparentGzip = true;
            builder.addHeader("Accept-Encoding", "gzip");
        }
        Response response = chain.proceed(builder.build().freeze());
        if (transparentGzip && "gzip".equalsIgnoreCase(response.header("Content-Encoding"))) {
            ResponseBody originalBody = response.body();
            if (originalBody != null && originalBody.stream() != null) {
                InputStream is = new GZIPInputStream(originalBody.stream());
                ResponseBody newBody = ResponseBody.create(is,
                        originalBody.contentLength(),
                        originalBody.contentType(),
                        originalBody.charset());
                response = response.newBuilder()
                        .body(newBody)
                        .removeHeader("Content-Encoding")
                        .removeHeader("Content-Length")
                        .build();
            }
        }
        return response;
    }
}
