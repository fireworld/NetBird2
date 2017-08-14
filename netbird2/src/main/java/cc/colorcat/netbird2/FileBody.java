package cc.colorcat.netbird2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by cxx on 16-12-15.
 * xx.ch@outlook.com
 */
public final class FileBody extends RequestBody {

    static FileBody create(String name, String contentType, File file, UploadListener listener) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("file is not exists");
        }
        Utils.nonNull(name, "name == null");
        Utils.nonNull(contentType, "contentType == null");
        return new FileBody(name, contentType, file, listener);
    }

    final String name;
    final File file;
    final String contentType;
    private final UploadListener listener;
    private long contentLength = -1L;

    private FileBody(String name, String contentType, File file, UploadListener listener) {
        this.name = name;
        this.file = file;
        this.contentType = contentType;
        this.listener = listener;
    }

    @Override
    public String contentType() {
        return contentType;
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
            is = ProgressInputStream.wrap(is, contentLength(), listener);
            Utils.justDump(is, os);
        } finally {
            Utils.close(is);
        }
    }

    public String name() {
        return name;
    }

    public File file() {
        return file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileBody fileBody = (FileBody) o;

        if (!name.equals(fileBody.name)) return false;
        if (!contentType.equals(fileBody.contentType)) return false;
        return file.getAbsolutePath().equals(fileBody.file.getAbsolutePath());
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + contentType.hashCode();
        result = 31 * result + file.getAbsolutePath().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FileBody{" +
                "name='" + name + '\'' +
                ", contentType='" + contentType + '\'' +
                ", file=" + file.getAbsolutePath() +
                '}';
    }
}
