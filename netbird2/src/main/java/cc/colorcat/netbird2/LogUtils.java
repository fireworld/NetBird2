package cc.colorcat.netbird2;

import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
class LogUtils {
    static final int VERBOSE = 1;
    static final int DEBUG = 2;
    static final int INFO = 3;
    static final int WARN = 4;
    static final int ERROR = 5;
    static final int NOTHING = 6;
    private static int level = VERBOSE;

    static void v(String tag, String msg) {
        if (VERBOSE >= level) {
            Log.v(tag, msg);
        }
    }

    static void d(String tag, String msg) {
        if (DEBUG >= level) {
            Log.d(tag, msg);
        }
    }

    static void i(String tag, String msg) {
        if (INFO >= level) {
            Log.i(tag, msg);
        }
    }

    static void w(String tag, String msg) {
        if (WARN >= level) {
            Log.w(tag, msg);
        }
    }

    static void e(String tag, String msg) {
        if (ERROR >= level) {
            Log.e(tag, msg);
        }
    }

    static void e(Throwable e) {
        if (ERROR >= level) {
            e.printStackTrace();
        }
    }

    static void dd(String tag, String msg) {
        Log.d(tag, msg);
    }

    static void ii(String tag, String msg) {
        Log.i(tag, msg);
    }

    static void setLevel(@Level int level) {
        LogUtils.level = level;
    }

    @Level
    static int getLevel() {
        return LogUtils.level;
    }

    private LogUtils() {
        throw new AssertionError("no instance.");
    }

    @IntDef({VERBOSE, DEBUG, INFO, WARN, ERROR, NOTHING})
    @Retention(RetentionPolicy.SOURCE)
    @interface Level {
    }
}
