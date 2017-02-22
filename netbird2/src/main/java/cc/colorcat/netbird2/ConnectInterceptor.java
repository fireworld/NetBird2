package cc.colorcat.netbird2;

import java.io.IOException;

import cc.colorcat.netbird2.connection.Connection;
import cc.colorcat.netbird2.request.Request;
import cc.colorcat.netbird2.response.Response;
import cc.colorcat.netbird2.response.ResponseBody;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public class ConnectInterceptor implements Interceptor {
    private NetBird netBird;

    public ConnectInterceptor(NetBird netBird) {
        this.netBird = netBird;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request<?> req = chain.request();
        Connection connection = netBird.connection.clone();
        connection.connect(netBird, req);
        connection.writeHeaders(req.headers());
        if (req.method() == Request.Method.POST) {
            connection.writeBody(req.body());
            connection.flush();
        }
        int code = connection.responseCode();
        String msg = connection.responseMsg();
        Headers headers = null;
        ResponseBody body = null;
        if (code == 200) {
            headers = connection.responseHeaders();
            body = connection.responseBody(headers);
        }
        return new Response.Builder().code(code).msg(msg).headers(headers).body(body).build();
    }
}
