package cc.colorcat.netbird2;

import java.io.IOException;

import cc.colorcat.netbird2.Connection;
import cc.colorcat.netbird2.Headers;
import cc.colorcat.netbird2.Interceptor;
import cc.colorcat.netbird2.Method;
import cc.colorcat.netbird2.NetBird;
import cc.colorcat.netbird2.Request;
import cc.colorcat.netbird2.Response;
import cc.colorcat.netbird2.ResponseBody;

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
        if (req.method() == Method.POST) {
            conn.writeBody(req.body());
        }
        int code = conn.responseCode();
        String msg = conn.responseMsg();
        Headers headers = null;
        ResponseBody body = null;
        if (code == 200) {
            headers = conn.responseHeaders();
            body = conn.responseBody(headers);
        }
        return new Response.Builder().code(code).msg(msg).headers(headers).body(body).build();
    }
}
