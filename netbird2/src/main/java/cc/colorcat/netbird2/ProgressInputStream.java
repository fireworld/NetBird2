package cc.colorcat.netbird2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by cxx on 2016/12/12.
 * xx.ch@outlook.com
 */
public final class ProgressInputStream extends InputStream {
    private InputStream delegate;
    private ProgressListener listener;
    private final long contentLength;
    private long finished = 0;
    private int currentPercent;
    private int lastPercent = currentPercent;

    /**
     * @param is            数据读取的来源
     * @param contentLength is 所包含的数据总长度
     * @param listener      读取数据进度监听器
     * @return ProgressInputStream
     */
    public static InputStream wrap(InputStream is, long contentLength, ProgressListener listener) {
        if (is != null && contentLength > 0 && listener != null) {
            return new ProgressInputStream(is, contentLength, listener);
        }
        return is;
    }

    /**
     * @param file     数据来源于此文件
     * @param listener 读取数据进度监听器
     * @return ProgressInputStream
     */
    public static InputStream wrap(File file, ProgressListener listener) throws FileNotFoundException {
        InputStream is = new FileInputStream(file);
        if (listener != null) {
            is = new ProgressInputStream(is, file.length(), listener);
        }
        return is;
    }

    private ProgressInputStream(@NonNull InputStream is, long contentLength, ProgressListener listener) {
        this.delegate = Utils.nonNull(is, "is == null");
        this.contentLength = contentLength;
        this.listener = listener;
    }

    @Override
    public int read(@NonNull byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
        int read = delegate.read(b, off, len);
        finished += read;
        currentPercent = (int) (finished * 100 / contentLength);
        if (currentPercent > lastPercent) {
            Utils.postProgress(listener, finished, contentLength, currentPercent);
            lastPercent = currentPercent;
        }
        return read;
    }

    @Override
    public long skip(long n) throws IOException {
        return delegate.skip(n);
    }

    @Override
    public int available() throws IOException {
        return delegate.available();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public void mark(int readLimit) {
        delegate.mark(readLimit);
    }

    @Override
    public void reset() throws IOException {
        delegate.reset();
    }

    @Override
    public boolean markSupported() {
        return delegate.markSupported();
    }

    @Override
    public int read() throws IOException {
        return delegate.read();
    }
}
