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
        deliver(request, data);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        int code = Const.CODE_IO_ERROR;
        String msg = Utils.emptyElse(e.getMessage(), Const.MSG_IO_ERROR);
        if (Const.MSG_DUPLICATE_REQUEST.equals(msg)) {
            code = Const.CODE_DUPLICATE_REQUEST;
        }
        deliver(call.request(), NetworkData.newFailure(code, msg));
    }

    @SuppressWarnings("unchecked")
    private static void deliver(Request<?> request, NetworkData data) {
        request.deliver(data);
    }
}
