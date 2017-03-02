package cc.colorcat.netbird2;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by cxx on 17-2-23.
 * xx.ch@outlook.com
 */
public final class StringParser implements Parser<String> {
    private static transient StringParser utf8;

    public static StringParser create(@NonNull String charset) {
        return new StringParser(Charset.forName(charset));
    }

    public static StringParser getUtf8() {
        if (utf8 == null) {
            synchronized (StringParser.class) {
                if (utf8 == null) {
                    utf8 = new StringParser(Charset.forName(Const.UTF8));
                }
            }
        }
        return utf8;
    }

    private Charset charset;

    private StringParser(Charset charset) {
        this.charset = charset;
    }

    @NonNull
    @Override
    public NetworkData<? extends String> parse(@NonNull Response data) throws IOException {
        return NetworkData.newSuccess(data.body().string(charset));
    }
}