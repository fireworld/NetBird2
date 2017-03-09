package cc.colorcat.netbird2;

import android.support.annotation.NonNull;

import java.io.IOException;

/**
 * 将 Response 解析为目标数据
 * <p>
 * Created by cxx on 17-2-23.
 * xx.ch@outlook.com
 */
public interface Parser<T> {
    /**
     * 将 {@link Response} 解析为目标数据
     * 如果解析的数据不为空调用 {@link NetworkData#newSuccess(T)} 并返回
     * 否则调用 {@link NetworkData#newFailure(int, String)} 并返回
     *
     * @see BitmapParser
     * @see FileParser
     * @see StringParser
     */
    @NonNull
    NetworkData<? extends T> parse(@NonNull Response data) throws IOException;
}
