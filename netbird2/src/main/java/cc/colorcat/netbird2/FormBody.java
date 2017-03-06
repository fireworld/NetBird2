package cc.colorcat.netbird2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by cxx on 16-12-15.
 * xx.ch@outlook.com
 */
final class FormBody extends RequestBody {
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
//    private static final String CONTENT_TYPE = "text/plain; charset=UTF-8";

    public static FormBody create(Parameters namesAndValues) {
        return new FormBody(namesAndValues);
    }

    private final Parameters namesAndValues;
    private long contentLength = -1L;

    private FormBody(Parameters namesAndValues) {
        this.namesAndValues = namesAndValues;
    }

    public int size() {
        return namesAndValues.size();
    }

    public String name(int index) {
        return namesAndValues.name(index);
    }

    public String value(int index) {
        return namesAndValues.value(index);
    }

    @Override
    public String contentType() {
        return CONTENT_TYPE;
    }

    @Override
    public long contentLength() throws IOException {
        if (contentLength == -1L) {
            long length = writeOrCountBytes(null, true);
            if (length > 0L) {
                contentLength = length;
            }
        }
        return contentLength;
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        writeOrCountBytes(os, false);
    }

    private long writeOrCountBytes(OutputStream os, boolean countBytes) throws IOException {
        long byteCount = 0L;

        OutputStream o = countBytes ? new ByteArrayOutputStream() : os;
        ByteOutputStream bos = new ByteOutputStream(o);

        for (int i = 0, size = namesAndValues.size(); i < size; i++) {
            if (i > 0) bos.writeByte('&');
            bos.writeUtf8(namesAndValues.name(i));
            bos.writeByte('=');
            bos.writeUtf8(namesAndValues.value(i));
        }
        bos.flush();

        if (countBytes) {
            byteCount = bos.size();
            bos.close();
        }

        return byteCount;
    }
}
