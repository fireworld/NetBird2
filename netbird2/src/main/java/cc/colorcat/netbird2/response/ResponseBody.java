package cc.colorcat.netbird2.response;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public abstract class ResponseBody {

    public abstract long contentLength();

    public abstract String contentType();

    public abstract InputStream stream();

    public abstract Reader reader();

    public abstract String string() throws IOException;

    public abstract byte[] bytes() throws IOException;
}
