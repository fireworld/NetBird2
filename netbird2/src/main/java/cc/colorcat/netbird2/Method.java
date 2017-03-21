package cc.colorcat.netbird2;

/**
 * Created by cxx on 2017/2/24.
 * xx.ch@outlook.com
 */
public enum Method {
    GET, POST, PUT, PATCH, DELETE;

    public boolean requiresRequestBody() {
        switch (this) {
            case POST:
            case PUT:
            case PATCH:
            case DELETE:
                return true;
            default:
                return false;
        }
    }
}
