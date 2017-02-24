package cc.colorcat.netbird2;

import java.io.Closeable;
import java.io.IOException;

import cc.colorcat.netbird2.meta.Headers;
import cc.colorcat.netbird2.request.Request;
import cc.colorcat.netbird2.request.RequestBody;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
public interface Connection extends Closeable, Cloneable {

    void connect(NetBird netBird, Request<?> request) throws IOException;

    int responseCode() throws IOException;

    String responseMsg() throws IOException;

    void writeHeaders(Headers headers) throws IOException;

    void writeBody(RequestBody body) throws IOException;

    void cancel();

    Headers responseHeaders() throws IOException;

    ResponseBody responseBody(Headers headers) throws IOException;

    Connection clone();
}
