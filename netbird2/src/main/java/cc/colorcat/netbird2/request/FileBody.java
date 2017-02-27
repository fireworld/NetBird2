package cc.colorcat.netbird2.request;

import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cc.colorcat.netbird2.InputWrapper;
import cc.colorcat.netbird2.util.Utils;

/**
 * Created by cxx on 16-12-15.
 * xx.ch@outlook.com
 */
final class FileBody extends RequestBody {
    private String name;
    private File file;
    private String type;
    private UploadListener listener;
    private long contentLength = -1L;

    static FileBody create(Request.Pack pack, @Nullable UploadListener listener) {
        return new FileBody(pack.name, pack.file, pack.contentType, listener);
    }

    private FileBody(String name, File file, String type, UploadListener listener) {
        this.name = name;
        this.file = file;
        this.type = type;
        this.listener = listener;
    }

    @Override
    public String contentType() {
        return type;
    }

    @Override
    public long contentLength() throws IOException {
        if (contentLength == -1L) {
            long length = file.length();
            if (length > 0L) {
                contentLength = length;
            }
        }
        return contentLength;
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            if (listener != null) {
                long contentLength = contentLength();
                if (contentLength > 0) {
                    is = InputWrapper.create(is, contentLength, listener);
                }
            }
            Utils.justDump(is, os);
        } finally {
            Utils.close(is);
        }
    }

    String name() {
        return name;
    }

    String fileName() {
        return file.getName();
    }
}
