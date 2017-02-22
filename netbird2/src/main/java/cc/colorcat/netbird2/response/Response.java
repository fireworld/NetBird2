package cc.colorcat.netbird2.response;

import cc.colorcat.netbird2.Const;
import cc.colorcat.netbird2.Headers;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */

public class Response {
    private int code;
    private String msg;
    private Headers headers;
    private ResponseBody body;

    private Response(Builder builder) {
        this.code = builder.code;
        this.msg = builder.msg;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public static class Builder {
        private int code = Const.CODE_CONNECT_ERROR;
        private String msg = Const.MSG_CONNECT_ERROR;
        private Headers headers;
        private ResponseBody body;

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder headers(Headers headers) {
            this.headers = headers;
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
