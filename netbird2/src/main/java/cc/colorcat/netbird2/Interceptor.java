package cc.colorcat.netbird2;

import java.io.IOException;

import cc.colorcat.netbird2.request.Request;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
public interface Interceptor {
    Response intercept(Chain chain) throws IOException;

    interface Chain {
        Connection connection();

        Request<?> request();

        Response proceed(Request<?> request) throws IOException;
    }
}
