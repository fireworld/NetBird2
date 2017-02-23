package cc.colorcat.netbird2;

import android.support.annotation.NonNull;

/**
 * Created by cxx on 17-2-23.
 * xx.ch@outlook.com
 */
public interface Parser<T> {
    /**
     * 将 {@link Response} 解析为目标数据
     *
     * @see cc.colorcat.netbird.parser.BitmapParser
     * @see cc.colorcat.netbird.parser.FileParser
     * @see cc.colorcat.netbird.parser.JsonParser
     * @see cc.colorcat.netbird.parser.StringParser
     */
    @NonNull
    NetworkData<? extends T> parse(@NonNull Response data);
}
