package cc.colorcat.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import cc.colorcat.netbird2.NetworkData;
import cc.colorcat.netbird2.Parser;
import cc.colorcat.netbird2.Response;

/**
 * Created by cxx on 17-2-23.
 * xx.ch@outlook.com
 */
public class BitmapParser implements Parser<Bitmap> {
    private static BitmapParser parser;

    public static BitmapParser getParser() {
        if (parser == null) {
            parser = new BitmapParser();
        }
        return parser;
    }

    private BitmapParser() {

    }

    @NonNull
    @Override
    public NetworkData<? extends Bitmap> parse(@NonNull Response data) {
        Bitmap bitmap = BitmapFactory.decodeStream(data.body().stream());
        if (bitmap != null) {
            return NetworkData.newSuccess(bitmap);
        } else {
            return NetworkData.newFailure(data.code(), data.msg());
        }
    }
}
