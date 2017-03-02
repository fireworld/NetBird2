package cc.colorcat.netbird2;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
public final class Const {
    public static final String UTF8 = "UTF-8";

    public static final int CODE_CONNECT_ERROR = -100;
    public static final String MSG_CONNECT_ERROR = "connect error";

    public static final int CODE_DUPLICATE_REQUEST = -101;
    public static final String MSG_DUPLICATE_REQUEST = "duplicate request";

    public static final int CODE_CANCELED = -102;
    public static final String MSG_CANCELED = "request canceled";

    private Const() {
        throw new AssertionError("no instance");
    }
}
