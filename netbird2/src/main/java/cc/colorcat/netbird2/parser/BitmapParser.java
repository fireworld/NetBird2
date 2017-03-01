package cc.colorcat.netbird2.parser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import java.io.IOException;

import cc.colorcat.netbird2.response.NetworkData;
import cc.colorcat.netbird2.response.Response;

/**
 * Created by cxx on 17-2-23.
 * xx.ch@outlook.com
 */
public final class BitmapParser implements Parser<Bitmap> {
    private static final BitmapParser PARSER = new BitmapParser();

    public static BitmapParser get() {
        return PARSER;
    }

    private BitmapParser() {
    }

    @NonNull
    @Override
    public NetworkData<? extends Bitmap> parse(@NonNull Response data) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(data.body().stream());
        if (bitmap != null) {
            return NetworkData.newSuccess(bitmap);
        } else {
            return NetworkData.newFailure(data.code(), data.msg());
        }
    }
}
