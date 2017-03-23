package cc.colorcat.demo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import cc.colorcat.netbird2.Connection;
import cc.colorcat.netbird2.Headers;
import cc.colorcat.netbird2.LoadListener;
import cc.colorcat.netbird2.NetBird;
import cc.colorcat.netbird2.RealResponseBody;
import cc.colorcat.netbird2.Request;
import cc.colorcat.netbird2.RequestBody;
import cc.colorcat.netbird2.ResponseBody;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;
import okio.BufferedSink;

/**
 * Created by cxx on 17-3-23.
 * xx.ch@outlook.com
 */
public class OkHttpConnection implements Connection {
    private boolean initialized = false;
    private OkHttpClient client;
    private okhttp3.Request.Builder builder;
    private String method;
    private Call call;
    private Response response;
    private InputStream is;
    private LoadListener listener;

    public OkHttpConnection() {
        client = new OkHttpClient.Builder().build();
    }

    private OkHttpConnection(OkHttpClient client, boolean initialized) {
        this.client = client;
        this.initialized = initialized;
    }

    @Override
    public void connect(NetBird netBird, Request request) throws IOException {
        listener = request.loadListener();
        if (!initialized) configOkHttpClient(netBird);
        builder = new okhttp3.Request.Builder().url(request.url()).tag(request.tag());
        method = request.method().name();
    }

    private synchronized void configOkHttpClient(NetBird netBird) {
        if (initialized) return;
        int connectTimeOut = netBird.connectTimeOut();
        int readTimeOut = netBird.readTimeOut();
        OkHttpClient.Builder builder = client.newBuilder()
                .connectTimeout(connectTimeOut, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                .writeTimeout(readTimeOut, TimeUnit.MILLISECONDS);
        Proxy proxy = netBird.proxy();
        if (proxy != null) {
            builder.proxy(proxy);
        }
        SSLSocketFactory factory = netBird.sslSocketFactory();
        if (factory != null) {
            builder.sslSocketFactory(factory, Platform.get().trustManager(factory));
        }
        HostnameVerifier verifier = netBird.hostnameVerifier();
        if (verifier != null) {
            builder.hostnameVerifier(verifier);
        }
        File cacheDir = netBird.cachePath();
        long cacheSize = netBird.cacheSize();
        if (cacheSize > 0L) {
            builder.cache(new Cache(cacheDir, cacheSize));
        }
        client = builder.build();
        initialized = true;
    }

    @Override
    public int responseCode() throws IOException {
        return execute().code();
    }

    @Override
    public String responseMsg() throws IOException {
        return execute().message();
    }

    @Override
    public void writeHeaders(Headers headers) throws IOException {
        for (int i = 0, size = headers.size(); i < size; ++i) {
            builder.addHeader(headers.name(i), headers.value(i));
        }
    }

    @Override
    public void writeBody(final RequestBody body) throws IOException {
        builder.method(method, new okhttp3.RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse(body.contentType());
            }

            @Override
            public long contentLength() throws IOException {
                return body.contentLength();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                body.writeTo(sink.outputStream());
                sink.flush();
            }
        });
    }

    @Override
    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

    @Override
    public Headers responseHeaders() throws IOException {
        Map<String, List<String>> map = execute().headers().toMultimap();
        return map != null ? Headers.create(map) : Headers.emptyHeaders();
    }

    @Override
    public ResponseBody responseBody(Headers headers) throws IOException {
        if (is == null) {
            okhttp3.ResponseBody body = execute().body();
            if (body != null) {
                is = body.byteStream();
            }
        }
        return RealResponseBody.create(is, headers, listener);
    }

    private Response execute() throws IOException {
        if (response == null) {
            call = client.newCall(builder.build());
            response = call.execute();
        }
        return response;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Connection clone() {
        return new OkHttpConnection(client, initialized);
    }

    @Override
    public void close() throws IOException {
        IoUtils.close(is, response);
    }
}
