package cc.colorcat.netbird2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cxx on 17-2-23.
 * xx.ch@outlook.com
 */
public final class BitmapParser implements Parser<Bitmap> {
    private static transient BitmapParser parser;

    public static BitmapParser get() {
        if (parser == null) {
            synchronized (BitmapParser.class) {
                parser = new BitmapParser();
            }
        }
        return parser;
    }

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
            bitmap = decodeStream(data.body().stream(), reqWidth, reqHeight);
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
        if (!options.mCancel && options.outWidth != -1 && options.outHeight != -1) {
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
