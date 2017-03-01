package cc.colorcat.netbird2.response;

import java.io.InputStream;
import java.nio.charset.Charset;

import cc.colorcat.netbird2.InputWrapper;
import cc.colorcat.netbird2.meta.Headers;

/**
 * Created by cxx on 2017/2/24.
 * xx.ch@outlook.com
 */
public class RealResponseBody extends ResponseBody {

    public static RealResponseBody create(InputStream is, Headers headers) {
        return is != null ? new RealResponseBody(is, headers) : null;
    }

    public static RealResponseBody create(InputStream is, Headers headers, LoadListener listener) {
        if (is == null) return null;
        InputStream data = is;
        if (listener != null) {
            long contentLength = headers.contentLength();
            if (contentLength > 0) {
                data = InputWrapper.create(data, contentLength, listener);
            }
        }
        return new RealResponseBody(data, headers);
    }

    private final InputStream is;
    private final Headers headers;

    protected RealResponseBody(InputStream is, Headers headers) {
        this.is = is;
        this.headers = headers;
    }

    @Override
    public long contentLength() {
        return headers.contentLength();
    }

    @Override
    public String contentType() {
        return headers.contentType();
    }

    @Override
    public Charset charset() {
        return headers.charset();
    }

    @Override
    public InputStream stream() {
        return is;
    }
}
