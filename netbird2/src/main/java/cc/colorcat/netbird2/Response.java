package cc.colorcat.netbird2;

import java.io.Closeable;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public class Response implements Closeable {
    private final int code;
    private final String msg;
    private final Headers headers;
    private final ResponseBody body;

    private Response(Builder builder) {
        this.code = builder.code;
        this.msg = builder.msg;
        this.headers = builder.headers.newReadableHeaders();
        this.body = builder.body;
    }

    public int code() {
        return code;
    }

    public String msg() {
        return msg;
    }

    public Headers headers() {
        return headers;
    }

    public ResponseBody body() {
        return body;
    }

    public String header(String name) {
        return headers.value(name);
    }

    public String header(String name, String defaultValue) {
        return headers.value(name, defaultValue);
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    @Override
    public void close() {
        if (body != null) {
            body.close();
        }
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", headers=" + headers +
                ", body=" + body +
                '}';
    }

    public static class Builder {
        private int code;
        private String msg;
        private WritableHeaders headers;
        private ResponseBody body;

        public Builder() {
            headers = WritableHeaders.create(16);
            code = Const.CODE_CONNECT_ERROR;
            msg = Const.MSG_CONNECT_ERROR;
        }

        private Builder(Response response) {
            code = response.code;
            msg = response.msg;
            headers = response.headers.newWritableHeaders();
            body = response.body;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder headers(Headers headers) {
            this.headers.addAll(headers.names(), headers.values());
            return this;
        }

        public Builder addHeader(String name, String value) {
            Utils.checkHeader(name, value);
            headers.add(name, value);
            return this;
        }

        public Builder setHeader(String name, String value) {
            Utils.checkHeader(name, value);
            headers.set(name, value);
            return this;
        }

        public Builder addHeaderIfNot(String name, String value) {
            Utils.checkHeader(name, value);
            headers.addIfNot(name, value);
            return this;
        }

        public Builder body(ResponseBody body) {
            this.body = body;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }
}
