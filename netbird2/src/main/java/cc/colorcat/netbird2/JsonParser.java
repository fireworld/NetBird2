package cc.colorcat.netbird2;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by cxx on 2017/3/23.
 * xx.ch@outlook.com
 */
public final class JsonParser implements Parser<JSONObject> {
    private static volatile JsonParser instance;
    private static volatile JsonParser utf8;

    public static JsonParser create(@NonNull String charset) {
        return new JsonParser(Charset.forName(charset));
    }

    public static JsonParser get() {
        if (instance == null) {
            synchronized (JsonParser.class) {
                if (instance == null) {
                    instance = new JsonParser(null);
                }
            }
        }
        return instance;
    }

    public static JsonParser getUtf8() {
        if (utf8 == null) {
            synchronized (JsonParser.class) {
                if (utf8 == null) {
                    utf8 = new JsonParser(Charset.forName(Utils.UTF8));
                }
            }
        }
        return utf8;
    }

    private final Charset charset;

    private JsonParser(Charset charset) {
        this.charset = charset;
    }

    @NonNull
    @Override
    public NetworkData<? extends JSONObject> parse(@NonNull Response data) throws IOException {
        String s = charset == null ? data.body().string() : data.body().string(charset);
        try {
            JSONObject obj = new JSONObject(s);
            return NetworkData.newSuccess(obj);
        } catch (JSONException e) {
            LogUtils.e(e);
            throw new IOException(e);
        }
    }
}
