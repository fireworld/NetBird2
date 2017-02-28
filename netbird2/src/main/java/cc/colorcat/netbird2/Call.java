package cc.colorcat.netbird2;

import java.io.Closeable;
import java.io.IOException;

import cc.colorcat.netbird2.request.Request;
import cc.colorcat.netbird2.response.Response;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
public interface Call extends Closeable {
    Request<?> request();

    void enqueue(Callback callback);

    Response execute() throws IOException;

    void cancel();

    interface Factory {
        Call newCall(Request<?> request);
    }
}
