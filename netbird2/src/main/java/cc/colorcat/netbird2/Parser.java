package cc.colorcat.netbird2;

import android.support.annotation.NonNull;

import java.io.IOException;

/**
 * 数据解析器，将 Response 解析为目标数据
 * <p>
 * Created by cxx on 17-2-23.
 * xx.ch@outlook.com
 */
public interface Parser<T> {
    /**
     * 将 {@link Response} 解析为目标数据
     * 如果解析成功返回 {@link NetworkData#newSuccess(T)}
     * 否则返回 {@link NetworkData#newFailure(int, String)} 或抛出 {@link IOException} / {@link StateIOException}
     *
     * @see BitmapParser
     * @see FileParser
     * @see StringParser
     * @see JsonParser
     */
    @NonNull
    NetworkData<? extends T> parse(@NonNull Response data) throws IOException;
}
