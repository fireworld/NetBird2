package cc.colorcat.netbird2.connection;

import java.io.Closeable;
import java.io.IOException;

import cc.colorcat.netbird2.Headers;
import cc.colorcat.netbird2.NetBird;
import cc.colorcat.netbird2.request.Request;
import cc.colorcat.netbird2.request.RequestBody;
import cc.colorcat.netbird2.response.ResponseBody;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public interface Connection extends Closeable, Cloneable {

    void connect(NetBird netBird, Request<?> request) throws IOException;

    int responseCode() throws IOException;

    String responseMsg() throws IOException;

    void writeHeaders(Headers request) throws IOException;

    void writeBody(RequestBody body) throws IOException;

    void flush();

    void cancel();

    Headers responseHeaders() throws IOException;

    ResponseBody responseBody(Headers headers) throws IOException;

    Connection clone();
}
