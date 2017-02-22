package cc.colorcat.netbird2.request;

import cc.colorcat.netbird2.Headers;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public class Request<T> {

    public Headers headers() {
        return null;
    }

    public RequestBody body() {
        return null;
    }

    public Method method() {
        return null;
    }

    public enum Method {
        GET, POST
    }
}
