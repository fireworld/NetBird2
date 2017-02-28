package cc.colorcat.netbird2.request;

import android.support.annotation.NonNull;

/**
 * Created by cxx on 17-2-23.
 * xx.ch@outlook.com
 */
public interface Callback<R> {
    void onStart();

    void onSuccess(@NonNull R result);

    void onFailure(int code, @NonNull String msg);

    void onFinish();
}
