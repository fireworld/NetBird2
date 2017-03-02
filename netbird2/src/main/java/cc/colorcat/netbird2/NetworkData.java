package cc.colorcat.netbird2;

/**
 * Created by cxx on 17-2-23.
 * xx.ch@outlook.com
 */
public final class NetworkData<T> {
    final boolean isSuccess;
    final int code;
    final String msg;
    final T data;

    public static <R> NetworkData<? extends R> newSuccess(R data) {
        return new NetworkData<>(200, "ok", Utils.nonNull(data, "data == null"));
    }

    public static <R> NetworkData<? extends R> newFailure(int code, String msg) {
        return new NetworkData<>(code, Utils.nonEmpty(msg, "msg is empty"), null);
    }

    private NetworkData(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        isSuccess = (data != null);
    }
}
