package cc.colorcat.netbird2;

import android.support.annotation.NonNull;

import java.io.IOException;

/**
 * Created by cxx on 17-2-23.
 * xx.ch@outlook.com
 */
public interface Parser<T> {
    /**
     * 将 {@link Response} 解析为目标数据
     *
     * @see BitmapParser
     * @see FileParser
     * @see StringParser
     */
    @NonNull
    NetworkData<? extends T> parse(@NonNull Response data) throws IOException;
}
