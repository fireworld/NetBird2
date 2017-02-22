package cc.colorcat.netbird2;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public class Request<T> {

    public String url() {
        return null;
    }

    public String path() {
        return null;
    }

    public Headers headers() {
        return null;
    }

    public RequestBody body() {
        return null;
    }

    public Method method() {
        return null;
    }

    public String encodedParams() {
        return null;
    }

    public enum Method {
        GET, POST
    }
}
