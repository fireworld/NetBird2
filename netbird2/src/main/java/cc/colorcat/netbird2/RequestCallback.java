package cc.colorcat.netbird2;

import java.io.IOException;

/**
 * Created by cxx on 2017/2/28.
 * xx.ch@outlook.com
 */
final class RequestCallback implements Callback {

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Request request = call.request();
        NetworkData data = null;
        int code = response.code();
        String msg = response.msg();
        if (code == 200 && response.body() != null) {
            data = request.parser().parse(response);
        }
        if (data == null) {
            data = NetworkData.newFailure(code, msg);
        }
        Utils.deliverDataOnUi(request.listener(), data);
    }

    @Override
    public void onFailure(Call call, StateIOException e) {
        NetworkData data = NetworkData.newFailure(e.getState(), e.getMessage());
        Utils.deliverDataOnUi(call.request().listener(), data);
    }
}
