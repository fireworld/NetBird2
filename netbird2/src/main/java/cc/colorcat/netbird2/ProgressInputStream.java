package cc.colorcat.netbird2;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * 包装 {@link InputStream} 以显示流的读取进度。
 * <p>
 * Created by cxx on 2016/12/12.
 * xx.ch@outlook.com
 */
public final class ProgressInputStream extends FilterInputStream {
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
        super(is);
        this.contentLength = contentLength;
        this.listener = listener;
    }

    @Override
    public int read(@NonNull byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
        return updateProgress(in.read(b, off, len));
    }

    @Override
    public int read() throws IOException {
        return updateProgress(in.read());
    }

    private int updateProgress(final int read) {
        if (read != -1) {
            finished += read;
            currentPercent = (int) (finished * 100 / contentLength);
            if (currentPercent > lastPercent) {
                HandlerUtils.postProgress(listener, finished, contentLength, currentPercent);
                lastPercent = currentPercent;
            }
        }
        return read;
    }
}
