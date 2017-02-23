package cc.colorcat.demo;

import android.support.annotation.NonNull;

import java.io.IOException;

import cc.colorcat.netbird2.NetworkData;
import cc.colorcat.netbird2.Parser;
import cc.colorcat.netbird2.Response;
import cc.colorcat.netbird2.util.Utils;

/**
 * Created by cxx on 17-2-23.
 * xx.ch@outlook.com
 */
public class StringParser implements Parser<String> {
    private static StringParser utf8;

    public static StringParser create(@NonNull String charset) {
        return new StringParser(Utils.nonEmpty(charset, "charset is empty"));
    }

    public static StringParser getUtf8() {
        if (utf8 == null) {
            utf8 = new StringParser("UTF-8");
        }
        return utf8;
    }

    private String charset;

    private StringParser(String charset) {
        this.charset = charset;
    }

    @NonNull
    @Override
    public NetworkData<? extends String> parse(@NonNull Response data) {
        try {
            String value = data.body().string();
            return NetworkData.newSuccess(value);
        } catch (IOException e) {
            return NetworkData.newFailure(data.code(), Utils.formatMsg(data.msg(), e));
        }
    }
}