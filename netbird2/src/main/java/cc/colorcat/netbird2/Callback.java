package cc.colorcat.netbird2;

import java.io.IOException;

/**
 * Created by cxx on 17-2-28.
 * xx.ch@outlook.com
 */

public interface Callback {

    /**
     * 如果此方法抛出了 {@link IOException} 将跳转至 {@link Callback#onFailure(Call, StateIOException)}
     */
    void onResponse(Call call, Response response) throws IOException;

    void onFailure(Call call, StateIOException e);
}
