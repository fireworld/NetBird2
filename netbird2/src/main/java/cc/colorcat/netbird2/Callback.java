package cc.colorcat.netbird2;

import java.io.IOException;

import cc.colorcat.netbird2.response.Response;

/**
 * Created by cxx on 17-2-28.
 * xx.ch@outlook.com
 */

public interface Callback {

    void onResponse(Call call, Response response);

    void onFailure(Call call, IOException e);
}
