package cc.colorcat.netbird2;

import java.io.IOException;

import cc.colorcat.netbird2.request.Request;
import cc.colorcat.netbird2.response.Response;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
public interface Call extends Cloneable {
    Request<?> request();

    Response execute() throws IOException;

    void enqueue(Callback callback);

    void cancel();

    interface Factory {
        Call newCall(Request<?> request);
    }
}
