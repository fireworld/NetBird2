package cc.colorcat.netbird2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public class Utils {

    public static String checkedHttp(String url) {
        if (!url.startsWith("http")) {
            throw new IllegalArgumentException("Bad url, the scheme must be http or https");
        }
        return url;
    }

    public static <T> T nonNull(T value, String msg) {
        if (value == null) {
            throw new NullPointerException(msg);
        }
        return value;
    }

    public static <T> List<T> immutableList(List<T> list) {
        return Collections.unmodifiableList(new ArrayList<>(list));
    }

    public static String formatMsg(String responseMsg, Exception e) {
        return String.format(Locale.getDefault(), "Response Msg: %s%nException Detail: %s", responseMsg, e);
    }

    private Utils() {
        throw new AssertionError("no instance");
    }
}
