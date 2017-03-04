package cc.colorcat.netbird2;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by cxx on 17-2-23.
 * xx.ch@outlook.com
 */
public final class FileParser implements Parser<File> {
    private File file;

    public static FileParser create(String savePath) {
        File file = new File(savePath);
        return create(file);
    }

    public static FileParser create(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        File parent = file.getParentFile();
        if (parent.exists() || parent.mkdirs()) {
            return new FileParser(file);
        }
        throw new RuntimeException("can't wrap directory: " + parent.getAbsolutePath());
    }

    private FileParser(File file) {
        this.file = file;
    }

    @NonNull
    @Override
    public NetworkData<? extends File> parse(@NonNull Response data) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            Utils.justDump(data.body().stream(), fos);
            return NetworkData.newSuccess(file);
        } finally {
            Utils.close(fos);
        }
    }
}
