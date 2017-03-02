package cc.colorcat.netbird2;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public abstract class RequestBody {

    public abstract String contentType();

    public long contentLength() throws IOException {
        return -1;
    }

    public abstract void writeTo(OutputStream os) throws IOException;
}
