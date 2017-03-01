package cc.colorcat.netbird2;

import java.io.IOException;

import cc.colorcat.netbird2.request.Request;
import cc.colorcat.netbird2.response.NetworkData;
import cc.colorcat.netbird2.response.Response;
import cc.colorcat.netbird2.util.Utils;

/**
 * Created by cxx on 2017/2/28.
 * xx.ch@outlook.com
 */
final class RequestCallback implements Callback {
    private static final NetworkData DATA_IO_ERROR;

    static {
        DATA_IO_ERROR = NetworkData.newFailure(Const.CODE_IO_ERROR, Const.MSG_IO_ERROR);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onResponse(Call call, Response response) {
        Request<?> request = call.request();
        NetworkData data = null;
        int code = response.code();
        String msg = response.msg();
        if (code == 200 && response.body() != null) {
            try {
                data = request.parse(response);
            } catch (IOException e) {
                msg = Utils.formatMsg(msg, e);
            } finally {
                response.close();
            }
        }
        if (data == null) {
            data = NetworkData.newFailure(code, msg);
        }
        request.deliver(data);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onFailure(Call call, IOException e) {
        Request<?> request = call.request();
        NetworkData data;
        if (e instanceof StateIOException) {
            data = NetworkData.newFailure(((StateIOException) e).getState(), e.getMessage());
        } else {
            data = DATA_IO_ERROR;
        }
        request.deliver(data);
    }
}
