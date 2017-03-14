package cc.colorcat.netbird2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
public final class HttpConnection implements Connection {
    private boolean enableCache = false;
    private HttpURLConnection conn;
    private InputStream is;
    private LoadListener listener;

    public HttpConnection() {

    }

    private HttpConnection(boolean enableCache) {
        this.enableCache = enableCache;
    }

    @Override
    public void connect(NetBird netBird, Request request) throws IOException {
        listener = request.loadListener();
        enableCache(netBird.cachePath(), netBird.cacheSize());
        URL url = new URL(request.url());
        Proxy proxy = netBird.proxy();
        conn = (HttpURLConnection) (proxy == null ? url.openConnection() : url.openConnection(proxy));
        conn.setConnectTimeout(netBird.connectTimeOut());
        conn.setReadTimeout(netBird.readTimeOut());
        conn.setDoInput(true);
        Method method = request.method();
        conn.setRequestMethod(method.name());
        conn.setDoOutput(method == Method.POST);
        conn.setUseCaches(enableCache);
    }

    @Override
    public int responseCode() throws IOException {
        return conn.getResponseCode();
    }

    @Override
    public String responseMsg() throws IOException {
        return conn.getResponseMessage();
    }

    @Override
    public void writeHeaders(Headers headers) throws IOException {
        for (int i = 0, size = headers.size(); i < size; i++) {
            String name = headers.name(i);
            String value = headers.value(i);
            conn.addRequestProperty(name, value);
        }
    }

    @Override
    public void writeBody(RequestBody body) throws IOException {
        if (body != null) {
            long contentLength = body.contentLength();
            if (contentLength > 0) {
                OutputStream os = null;
                try {
                    os = conn.getOutputStream();
                    body.writeTo(os);
                    os.flush();
                } finally {
                    Utils.close(os);
                }
            }
        }
    }

    @Override
    public void cancel() {
        if (conn != null) {
            conn.disconnect();
        }
    }

    @Override
    public Headers responseHeaders() throws IOException {
        Map<String, List<String>> map = conn.getHeaderFields();
        return map != null ? Headers.create(map) : Headers.emptyHeaders();
    }

    @Override
    public ResponseBody responseBody(Headers headers) throws IOException {
        if (is == null) {
            is = conn.getInputStream();
        }
        return RealResponseBody.create(is, headers, listener);
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Connection clone() {
        return new HttpConnection(enableCache);
    }

    @Override
    public void close() throws IOException {
        Utils.close(is);
        if (conn != null) {
            conn.disconnect();
        }
    }

    private void enableCache(File path, long cacheSize) {
        if (!enableCache && cacheSize > 0) {
            try {
                File cachePath = new File(path, "NetBird");
                Class.forName("android.net.http.HttpResponseCache")
                        .getMethod("install", File.class, long.class)
                        .invoke(null, cachePath, cacheSize);
                enableCache = true;
            } catch (Exception e) {
                enableCache = false;
                LogUtils.e(e);
            }
        }
    }
}
