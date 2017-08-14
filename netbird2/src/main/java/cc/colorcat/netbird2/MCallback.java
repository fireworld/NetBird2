package cc.colorcat.netbird2;

import java.io.IOException;

/**
 * Created by cxx on 2017/2/28.
 * xx.ch@outlook.com
 */
final class MCallback<T> implements Callback {
    private final Parser<? extends T> parser;
    private final MRequest.Listener<? super T> listener;
    private NetworkData<? extends T> data;

    MCallback(Parser<? extends T> parser, MRequest.Listener<? super T> listener) {
        this.parser = parser;
        this.listener = listener;
    }

    @Override
    public void onStart() {
        HandlerUtils.callStartOnUi(listener);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        int code = response.code();
        String msg = Utils.nullElse(response.msg(), "");
        if (code == 200 && response.body() != null) {
            data = parser.parse(response);
        }
        if (data == null) {
            data = NetworkData.newFailure(code, msg);
        }
    }

    @Override
    public void onFailure(Call call, StateIOException e) {
        data = NetworkData.newFailure(e.getState(), e.getMessage());
    }

    @Override
    public void onFinish() {
        HandlerUtils.deliverDataOnUi(listener, data);
    }
}
