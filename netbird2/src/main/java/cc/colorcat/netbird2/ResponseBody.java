package cc.colorcat.netbird2;

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

public class ResponseBody implements Closeable {
    private static final String CONTENT_TYPE = "Content-Type";
    private Headers headers;
    private InputStream is;

    public static ResponseBody create(Headers headers, InputStream is) {
        if (is == null) return null;
        return new ResponseBody(headers, is);
    }

    private ResponseBody(Headers headers, InputStream is) {
        this.headers = headers;
        this.is = is;
    }

    public long contentLength() {
        return Utils.quiteParse(headers.value("Content-Length"), -1L);
    }

    public String contentType() {
        return headers.value(CONTENT_TYPE);
    }

    public Charset charset() {
        String charset = Utils.parseCharset(headers.value(CONTENT_TYPE));
        if (charset == null) return null;
        try {
            return Charset.forName(charset);
        } catch (Exception ignore) {
            return null;
        }
    }

    public InputStream stream() {
        return is;
    }

    public Reader reader() {
        Charset charset = charset();
        return charset != null ? new InputStreamReader(is, charset) : new InputStreamReader(is);
    }

    public String string() throws IOException {
        return Utils.justRead(reader());
    }

    public byte[] bytes() throws IOException {
        return Utils.justRead(is);
    }

    @Override
    public void close() throws IOException {
        Utils.close(is);
    }


}
