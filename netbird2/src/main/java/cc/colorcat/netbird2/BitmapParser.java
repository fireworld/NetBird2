package cc.colorcat.netbird2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Parse the {@link Response} into {@link NetworkData<Bitmap>}.
 * <p>
 * Created by cxx on 17-2-23.
 * xx.ch@outlook.com
 */
public final class BitmapParser implements Parser<Bitmap> {
    private static volatile BitmapParser parser;

    /**
     * Gets the default {@link BitmapParser}
     */
    public static BitmapParser get() {
        if (parser == null) {
            synchronized (BitmapParser.class) {
                if (parser == null) {
                    parser = new BitmapParser();
                }
            }
        }
        return parser;
    }

    /**
     * 创建一个新的解析器
     *
     * @param reqWidth  解析后的 {@link Bitmap} 的宽度小于或等于此参数的值
     * @param reqHeight 解析后的 {@link Bitmap} 的高度小于或等于此参数的值
     * @throws IllegalArgumentException 如果 reqWidth / reqHeight 小于 1 将抛出此异常
     */
    public static BitmapParser create(int reqWidth, int reqHeight) {
        if (reqWidth < 1 || reqHeight < 1) {
            throw new IllegalArgumentException("reqWidth and reqHeight must be greater than 0");
        }
        return new BitmapParser(reqWidth, reqHeight);
    }

    private final int reqWidth;
    private final int reqHeight;

    private BitmapParser() {
        this(-1, -1);
    }

    private BitmapParser(int reqWidth, int reqHeight) {
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    @NonNull
    @Override
    public NetworkData<? extends Bitmap> parse(@NonNull Response data) throws IOException {
        NetworkData<? extends Bitmap> networkData;
        Bitmap bitmap;
        if (reqWidth > 0 && reqHeight > 0) {
            byte[] bytes = data.body().bytes();
            bitmap = decodeStream(new ByteArrayInputStream(bytes), reqWidth, reqHeight);
        } else {
            bitmap = BitmapFactory.decodeStream(data.body().stream());
        }
        if (bitmap != null) {
            networkData = NetworkData.newSuccess(bitmap);
        } else {
            networkData = NetworkData.newFailure(data.code(), data.msg());
        }
        return networkData;
    }

    @Nullable
    private static Bitmap decodeStream(@NonNull InputStream is, int reqWidth, int reqHeight) throws IOException {
        Bitmap result = null;
        BufferedInputStream bis = new BufferedInputStream(is);
        bis.mark(bis.available());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(bis, null, options);
        if (options.outWidth != -1 && options.outHeight != -1) {
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            bis.reset();
            result = BitmapFactory.decodeStream(bis, null, options);
        }
        return result;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
