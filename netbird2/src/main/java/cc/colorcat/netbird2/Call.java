package cc.colorcat.netbird2;

import java.io.Closeable;

import cc.colorcat.netbird2.request.Request;
import cc.colorcat.netbird2.response.Response;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public interface Call extends Closeable {

    Request<?> request();

    void enqueue();

    Response execute();

    void cancel();

    interface Factory {
        Call newCall(Request<?> request);
    }
}
