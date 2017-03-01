package cc.colorcat.netbird2;

import java.io.IOException;

import cc.colorcat.netbird2.request.Request;
import cc.colorcat.netbird2.response.NetworkData;
import cc.colorcat.netbird2.response.Response;

/**
 * Created by cxx on 2017/2/28.
 * xx.ch@outlook.com
 */
final class RequestCallback implements Callback {

    @SuppressWarnings("unchecked")
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Request<?> request = call.request();
        NetworkData data = null;
        int code = response.code();
        String msg = response.msg();
        if (code == 200 && response.body() != null) {
            data = request.parse(response);
        }
        if (data == null) {
            data = NetworkData.newFailure(code, msg);
        }
        request.deliver(data);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onFailure(Call call, StateIOException e) {
        NetworkData data = NetworkData.newFailure(e.getState(), e.getMessage());
        call.request().deliver(data);
    }
}
