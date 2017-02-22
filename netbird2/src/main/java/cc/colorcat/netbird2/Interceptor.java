package cc.colorcat.netbird2;

import cc.colorcat.netbird2.request.Request;
import cc.colorcat.netbird2.response.Response;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public interface Interceptor {

    Request<?> intercept(Request<?> request);

    Response intercept(Response response);
}
