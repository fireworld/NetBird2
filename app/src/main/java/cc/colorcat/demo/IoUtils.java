package cc.colorcat.demo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by cxx on 17-2-23.
 * xx.ch@outlook.com
 */
public class IoUtils {
    public static void dumpAndClose(InputStream is, OutputStream os) throws IOException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(os);
            byte[] buffer = new byte[2048];
            for (int length = bis.read(buffer); length != -1; length = bis.read(buffer)) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
        } finally {
            close(bis, bos);
        }
    }

    private static void close(Closeable c1, Closeable c2) {
        close(c1);
        close(c2);
    }

    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                LogUtils.e(e);
            }
        }
    }
}
