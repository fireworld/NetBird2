package cc.colorcat.netbird2;

import java.io.IOException;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
final class ConnectInterceptor implements Interceptor {
    private NetBird netBird;

    ConnectInterceptor(NetBird netBird) {
        this.netBird = netBird;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Connection conn = chain.connection();
        Request req = chain.request();
        conn.connect(netBird, req);
        conn.writeHeaders(req.headers());
        Method method = req.method();
        if (method.needBody()) {
            RequestBody body = req.body();
            if (body == null) {
                throw new IllegalArgumentException("method " + method.name()
                        + " must have a request body, but parameters and files are all empty.");
            }
            conn.writeBody(req.body());
        }
        int code = conn.responseCode();
        String msg = conn.responseMsg();
        Headers headers = Headers.emptyHeaders();
        ResponseBody body = null;
        if (code == 200) {
            headers = conn.responseHeaders();
            body = conn.responseBody(headers);
        }
        return new Response.Builder().code(code).msg(msg).headers(headers).body(body).build();
    }
}
