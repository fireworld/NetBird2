package cc.colorcat.netbird2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import cc.colorcat.netbird2.util.LogUtils;
import cc.colorcat.netbird2.util.Utils;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public class HttpConnection implements Connection {
    private boolean enableCache = false;
    private HttpURLConnection conn;
    private InputStream is;

    public HttpConnection() {

    }

    private HttpConnection(boolean enableCache) {
        this.enableCache = enableCache;
    }

    @Override
    public void connect(NetBird netBird, Request<?> request) throws IOException {
        enableCache(netBird.cachePath(), netBird.cacheSize());
        String url = Utils.nullElse(request.url(), netBird.baseUrl());
        String path = request.path();
        if (path != null) {
            url += path;
        }
        Request.Method method = request.method();
        if (method == Request.Method.GET) {
            String params = request.encodedParams();
            if (params != null) {
                url = url + '?' + params;
            }
        }
        conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(netBird.connectTimeOut());
        conn.setReadTimeout(netBird.readTimeOut());
        conn.setDoInput(true);
        conn.setRequestMethod(method.name());
        if (method == Request.Method.POST) {
            conn.setDoOutput(true);
        }
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
        if (headers != null && !headers.isEmpty()) {
            for (int i = 0, size = headers.size(); i < size; i++) {
                String name = headers.name(i);
                String value = headers.value(i);
                conn.addRequestProperty(name, value);
            }
        }
    }

    @Override
    public void writeBody(RequestBody body) throws IOException {
        if (body != null) {
            long contentLength = body.contentLength();
            if (contentLength > 0) {
                conn.setRequestProperty("Content-Type", body.contentType());
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
        conn.disconnect();
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
        return ResponseBody.create(headers, is);
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Connection clone() {
        return new HttpConnection(enableCache);
    }

    @Override
    public void close() throws IOException {
        Utils.close(is);
        conn.disconnect();
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
                LogUtils.e(e);
                enableCache = false;
            }
        }
    }
}