package cc.colorcat.netbird2.connection;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import cc.colorcat.netbird2.Headers;
import cc.colorcat.netbird2.NetBird;
import cc.colorcat.netbird2.request.Request;
import cc.colorcat.netbird2.request.RequestBody;
import cc.colorcat.netbird2.response.ResponseBody;
import cc.colorcat.netbird2.util.LogUtils;
import cc.colorcat.netbird2.util.Utils;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public class HttpConnection implements Connection {
    private boolean enableCache = false;
    private HttpURLConnection conn;

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
    public void writeHeaders(Headers request) throws IOException {

    }

    @Override
    public void writeBody(RequestBody body) throws IOException {

    }

    @Override
    public void flush() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public Headers responseHeaders() throws IOException {
        return null;
    }

    @Override
    public ResponseBody responseBody(Headers headers) throws IOException {
        return null;
    }

    @Override
    public Connection clone() {
        return new HttpConnection();
    }

    @Override
    public void close() throws IOException {

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
