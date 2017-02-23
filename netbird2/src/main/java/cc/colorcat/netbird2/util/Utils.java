package cc.colorcat.netbird2.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import cc.colorcat.netbird2.Const;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
public class Utils {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static void postOnUi(Runnable runnable) {
        HANDLER.post(runnable);
    }

    public static boolean isUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


    public static String smartEncode(String value) {
        try {
            String decodedValue = decode(value);
            if (!value.equals(decodedValue)) {
                return value;
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return encode(value);
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, Const.UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, Const.UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

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

    public static <T extends CharSequence> T nonEmpty(T txt, String msg) {
        if (Utils.isEmpty(txt)) {
            throw new IllegalArgumentException(msg);
        }
        return txt;
    }

    public static boolean isEmpty(CharSequence txt) {
        return txt == null || txt.length() == 0;
    }

    public static <T> T nullElse(T value, T other) {
        return value != null ? value : other;
    }

    public static <T extends CharSequence> T emptyElse(T txt, T other) {
        return isEmpty(txt) ? other : txt;
    }

    public static <T> List<T> immutableList(List<T> list) {
        return Collections.unmodifiableList(new ArrayList<>(list));
    }

    public static <T> List<T> safeImmutableList(List<T> list) {
        return list != null ? Collections.unmodifiableList(new ArrayList<>(list)) : Collections.<T>emptyList();
    }

    public static String formatMsg(String responseMsg, Exception e) {
        return String.format(Locale.getDefault(), "Response Msg: %s%nException Detail: %s", responseMsg, e);
    }

    public static void checkHeader(String name, String value) {
        if (name == null) throw new NullPointerException("name == null");
        if (name.isEmpty()) throw new IllegalArgumentException("name is empty");
        for (int i = 0, length = name.length(); i < length; i++) {
            char c = name.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                throw new IllegalArgumentException(String.format(Locale.getDefault(),
                        "Unexpected char %#04x at %d in header name: %s", (int) c, i, name));
            }
        }
        if (value == null) throw new NullPointerException("value == null");
        for (int i = 0, length = value.length(); i < length; i++) {
            char c = value.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                throw new IllegalArgumentException(String.format(Locale.getDefault(),
                        "Unexpected char %#04x at %d in %s value: %s", (int) c, i, name, value));
            }
        }
    }

    public static long quiteParse(String value, long defValue) {
        if (value == null) return defValue;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignore) {
            return defValue;
        }
    }

    @Nullable
    public static Charset charset(String charset) {
        try {
            return Charset.forName(charset);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static String parseCharset(String contentType) {
        if (contentType != null) {
            String[] params = contentType.split(";");
            final int length = params.length;
            for (int i = 1; i < length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equalsIgnoreCase("charset")) {
                        return pair[1];
                    }
                }
            }
        }
        return null;
    }

    public static void close(Closeable os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                LogUtils.e(e);
            }
        }
    }

    public static String justRead(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(reader);
        char[] buffer = new char[1024];
        for (int length = br.read(buffer); length != -1; length = br.read(buffer)) {
            sb.append(buffer, 0, length);
        }
        return sb.toString();
    }

    public static byte[] justRead(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length = bis.read(buffer); length != -1; length = bis.read(buffer)) {
            bos.write(buffer, 0, length);
        }
        bos.flush();
        return bos.toByteArray();
    }

    public static void justDump(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[2048];
        for (int length = is.read(buffer); length != -1; length = is.read(buffer)) {
            os.write(buffer, 0, length);
        }
    }

    private Utils() {
        throw new AssertionError("no instance");
    }
}
