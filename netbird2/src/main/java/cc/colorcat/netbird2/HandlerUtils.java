package cc.colorcat.netbird2;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by cxx on 17-3-5.
 * xx.ch@outlook.com
 */
final class HandlerUtils {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    static void callStartOnUi(final MRequest.Listener<?> listener) {
        if (listener != null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                listener.onStart();
            } else {
                HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onStart();
                    }
                });
            }
        }
    }

    static <T> void deliverDataOnUi(final MRequest.Listener<? super T> listener, final NetworkData<? extends T> data) {
        if (listener != null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                deliverData(listener, data);
            } else {
                HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        deliverData(listener, data);
                    }
                });
            }
        }
    }

    private static <T> void deliverData(MRequest.Listener<? super T> listener, NetworkData<? extends T> data) {
        if (data.isSuccess) {
            listener.onSuccess(data.data);
        } else {
            listener.onFailure(data.code, data.msg);
        }
        listener.onFinish();
    }

    static void postProgress(final ProgressListener listener,
                             final long finished, final long contentLength, final int percent) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            listener.onChanged(finished, contentLength, percent);
        } else {
            HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    listener.onChanged(finished, contentLength, percent);
                }
            });
        }
    }

    private HandlerUtils() {
        throw new AssertionError("no instance");
    }
}
