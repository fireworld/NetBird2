package cc.colorcat.netbird2.response;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import cc.colorcat.netbird2.util.Utils;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public abstract class ResponseBody implements Closeable {

    public abstract long contentLength();

    public abstract String contentType();

    public abstract Charset charset();

    public abstract InputStream stream();

    public final Reader reader() {
        Charset charset = charset();
        return charset != null ? new InputStreamReader(stream(), charset) : new InputStreamReader(stream());
    }

    public final Reader reader(Charset defCharset) {
        Charset charset = Utils.nullElse(charset(), defCharset);
        return new InputStreamReader(stream(), charset);
    }

    public final String string() throws IOException {
        return Utils.justRead(reader());
    }

    public final String string(Charset defCharset) throws IOException {
        return Utils.justRead(reader(defCharset));
    }

    public final byte[] bytes() throws IOException {
        return Utils.justRead(stream());
    }

    @Override
    public void close() {
        Utils.close(stream());
    }

    public static ResponseBody create(final InputStream is) {
        return create(is, null);
    }

    public static ResponseBody create(final InputStream is, final String contentType) {
        return create(is, -1L, contentType);
    }

    public static ResponseBody create(final InputStream is, final long contentLength, final String contentType) {
        if (is == null) throw new NullPointerException("is == null");
        return new ResponseBody() {
            @Override
            public long contentLength() {
                return contentLength;
            }

            @Override
            public String contentType() {
                return contentType;
            }

            @Override
            public Charset charset() {
                return Utils.parseCharset(contentType);
            }

            @Override
            public InputStream stream() {
                return is;
            }
        };
    }

    public static ResponseBody create(
            final InputStream is, final long contentLength, final String contentType, final Charset charset) {
        if (is == null) throw new NullPointerException("is == null");
        return new ResponseBody() {
            @Override
            public long contentLength() {
                return contentLength;
            }

            @Override
            public String contentType() {
                return contentType;
            }

            @Override
            public Charset charset() {
                return charset;
            }

            @Override
            public InputStream stream() {
                return is;
            }
        };
    }
}
