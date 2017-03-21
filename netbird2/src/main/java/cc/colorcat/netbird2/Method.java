package cc.colorcat.netbird2;

/**
 * Created by cxx on 2017/2/24.
 * xx.ch@outlook.com
 */
public enum Method {
    GET, HEAD, TRACE, OPTIONS, POST, PUT, DELETE;

    public boolean needBody() {
        switch (this) {
            case POST:
            case PUT:
            case DELETE:
                return true;
            default:
                return false;
        }
    }
}
