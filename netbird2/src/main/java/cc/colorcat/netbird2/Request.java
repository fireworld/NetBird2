package cc.colorcat.netbird2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
@SuppressWarnings("unused")
public class Request {
    private final String url;
    private final String path;
    private final Parameters params;
    private final Headers headers;
    private final List<FileBody> fileBodies;
    private final Method method;
    private final String boundary;
    private final LoadListener loadListener;
    private final Object tag;
    private RequestBody body;
    boolean freeze = false;

    protected Request(Builder builder) {
        this.url = builder.url;
        this.path = builder.path;
        this.params = builder.params.newReadableParameters();
        this.headers = builder.headers.newReadableHeaders();
        this.fileBodies = Utils.immutableList(builder.fileBodies);
        this.body = null;
        this.method = builder.method;
        this.boundary = builder.boundary;
        this.loadListener = builder.loadListener;
        this.tag = builder.tag;
    }

    public Builder newBuilder() {
        if (freeze) {
            throw new IllegalStateException("The request has been frozen, call isFreeze() to check.");
        }
        return new Builder(this);
    }

    final Request freeze() {
        freeze = true;
        return this;
    }

    final Request unfreeze() {
        freeze = false;
        return this;
    }

    public final boolean isFreeze() {
        return freeze;
    }

    public final String url() {
        return url;
    }

    public final String path() {
        return path;
    }

    public final Method method() {
        return method;
    }

    public final Parameters parameters() {
        return params;
    }

    public final Headers headers() {
        return headers;
    }

    public final List<FileBody> files() {
        return fileBodies;
    }

    public final LoadListener loadListener() {
        return loadListener;
    }

    public final RequestBody body() {
        if (body == null) {
            body = parseBody();
        }
        return body;
    }

    private RequestBody parseBody() {
        if (params.isEmpty() && fileBodies.isEmpty()) {
            return null;
        }
        if (params.isEmpty() && fileBodies.size() == 1) {
            return fileBodies.get(0);
        }
        if (!params.isEmpty() && fileBodies.isEmpty()) {
            return FormBody.create(params);
        }
        return MultipartBody.create(FormBody.create(params), fileBodies, boundary);
    }

    public final Object tag() {
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        if (url != null ? !url.equals(request.url) : request.url != null) return false;
        if (path != null ? !path.equals(request.path) : request.path != null) return false;
        if (method != request.method) return false;
        if (!params.equals(request.params)) return false;
        if (!headers.equals(request.headers)) return false;
        return fileBodies.equals(request.fileBodies);

    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + method.hashCode();
        result = 31 * result + params.hashCode();
        result = 31 * result + headers.hashCode();
        result = 31 * result + fileBodies.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", method=" + method +
                ", params=" + params +
                ", headers=" + headers +
                ", fileBodies=" + fileBodies +
                ", loadListener=" + loadListener +
                ", tag=" + tag +
                '}';
    }


    public static class Builder {
        private String url;
        private String path;
        private WritableParameters params;
        private WritableHeaders headers;
        private List<FileBody> fileBodies;
        private Method method;
        private String boundary;
        private LoadListener loadListener;
        private Object tag;

        protected Builder(Request req) {
            this.url = req.url;
            this.path = req.path;
            this.params = req.params.newWritableParameters();
            this.headers = req.headers.newWritableHeaders();
            this.fileBodies = new ArrayList<>(req.fileBodies);
            this.method = req.method;
            this.boundary = req.boundary;
            this.loadListener = req.loadListener;
            this.tag = req.tag;
        }

        public Builder() {
            this.params = WritableParameters.create(4);
            this.headers = WritableHeaders.create(4);
            this.fileBodies = new ArrayList<>(1);
            this.method = Method.GET;
            this.boundary = "==" + System.currentTimeMillis() + "==";
            this.tag = this.boundary;
        }

        /**
         * @param tag 请求的标记，可借此取消一个请求，如果没有配置将生成默认的 tag
         * @throws NullPointerException 如果 tag 为空将抛出此异常
         * @see NetBird#cancelWaiting(Object)
         * @see NetBird#cancelAll(Object)
         */
        public Builder tag(Object tag) {
            this.tag = Utils.nonNull(tag, "tag == null");
            return this;
        }

        /**
         * @param url 请求的 http/https 地址，如果未配置则使用构建 NetBird 时的 baseUrl
         * @throws IllegalArgumentException 如果 url 为空或不是以 "http" / "https" 开始将抛出此异常
         * @see NetBird#baseUrl()
         */
        public Builder url(String url) {
            this.url = Utils.checkedHttp(url);
            return this;
        }

        public String url() {
            return url;
        }

        /**
         * @param path 请求的路径
         */
        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public String path() {
            return path;
        }

        /**
         * @param method 请求方式，目前支持 GET, HEAD, TRACE, OPTIONS, POST, PUT, DELETE
         */
        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        public Method method() {
            return method;
        }

        /**
         * @param listener 下载进度监听器，服务器必须返回数据的长度才有效
         */
        public Builder loadListener(LoadListener listener) {
            loadListener = listener;
            return this;
        }

        /**
         * 添加请求参数
         *
         * @param name  请求参数的名称
         * @param value 请求参数的值
         * @throws IllegalArgumentException 如果 name/value 为 null 或空字符串将抛出此异常
         */
        public Builder add(String name, String value) {
            Utils.nonEmpty(name, "name is null/empty");
            Utils.nonEmpty(value, "value is null/empty");
            params.add(name, value);
            return this;
        }

        /**
         * 设置请求参数
         * 此操作将清除已添加的所有名称为 name 的参数对，然后添加所提供的参数对。
         *
         * @param name  请求参数的名称
         * @param value 请求参数的值
         * @throws IllegalArgumentException 如果 name/value 为 null 或空字符串将抛出此异常
         */
        public Builder set(String name, String value) {
            Utils.nonEmpty(name, "name is null/empty");
            Utils.nonEmpty(value, "value is null/empty");
            params.set(name, value);
            return this;
        }

        /**
         * 如果不存在名称为 name 的参数对则添加所提供的参数对，否则忽略之。
         *
         * @param name  请求参数的名称
         * @param value 请求参数的值
         * @throws IllegalArgumentException 如果 name/value 为 null 或空字符串将抛出此异常
         */
        public Builder addIfNot(String name, String value) {
            Utils.nonEmpty(name, "name is null/empty");
            Utils.nonEmpty(value, "value is null/empty");
            params.addIfNot(name, value);
            return this;
        }

        /**
         * 除清所有名称为 name 的参数对
         *
         * @param name 需要清除的参数的名称
         * @throws IllegalArgumentException 如果 name/value 为 null 或空字符串将抛出此异常
         */
        public Builder remove(String name) {
            Utils.nonEmpty(name, "name is null/empty");
            params.removeAll(name);
            return this;
        }

        /**
         * @return 返回所有请求参数的名称，不可修改，顺序与 {@link Builder#values()} 一一对应。
         */
        public final List<String> names() {
            return params.names();
        }

        /**
         * @return 返回所有请求参数的值，不可修改，顺序与 {@link Builder#names()} 一一对应。
         */
        public final List<String> values() {
            return params.values();
        }

        /**
         * @return 返回添加的与 name 对应的 value, 如果存在多个则返回先添加的，如果没有则返回 null
         * @throws IllegalArgumentException 如果 name 为 null或空字符串将抛出此异常
         */
        @Nullable
        public final String value(String name) {
            Utils.nonEmpty(name, "name is null/empty");
            return params.value(name);
        }

        /**
         * 清除所有已添加的请求参数
         */
        public Builder clear() {
            params.clear();
            return this;
        }

        /**
         * 添加一个需要上传的文件
         *
         * @param name        参数名
         * @param contentType 文件类型，如 "image/jpeg"
         * @param file        文件全路径
         * @throws IllegalArgumentException 如果 name/contentType 为 null 或空字符串，或 file 为 null 或不存在，均将抛出此异常。
         */
        public Builder addFile(String name, String contentType, File file) {
            return addFile(name, contentType, file, null);
        }

        /**
         * 添加需要上传的文件
         *
         * @param name        参数名
         * @param contentType 文件类型，如 "image/jpeg"
         * @param file        文件全路径
         * @throws IllegalArgumentException 如果 name/contentType 为 null 或空字符串，或 file 为 null 或不存在，均将抛出此异常。
         */
        public Builder addFile(String name, String contentType, File file, UploadListener listener) {
            fileBodies.add(FileBody.create(name, contentType, file, listener));
            return this;
        }

        /**
         * 清除所有已添加准备上传的文件
         */
        public Builder clearFile() {
            fileBodies.clear();
            return this;
        }

        /**
         * 添加一个请求 Header 参数，如果已添加了名称相同的 Header 不会清除之前的。
         *
         * @param name  Header 的名称
         * @param value Header 的值
         * @throws NullPointerException     如果 name/value 为 null, 将抛出此异常
         * @throws IllegalArgumentException 如果 name/value 不符合 Header 规范要求将抛出此异常
         */
        public Builder addHeader(String name, String value) {
            Utils.checkHeader(name, value);
            headers.add(name, value);
            return this;
        }

        /**
         * 设置一个请求 Header 参数，如果已添加了名称相同的 Header 则原来的都会被清除。
         *
         * @param name  Header 的名称
         * @param value Header 的值
         * @throws NullPointerException     如果 name/value 为 null, 将抛出此异常
         * @throws IllegalArgumentException 如果 name/value 不符合 Header 规范要求将抛出此异常
         */
        public Builder setHeader(String name, String value) {
            Utils.checkHeader(name, value);
            headers.set(name, value);
            return this;
        }

        /**
         * 如果不存在名称为 name 的 header 则添加，否则忽略之。
         *
         * @param name  Header 的名称
         * @param value Header 的值
         * @throws NullPointerException     如果 name/value 为 null, 将抛出此异常
         * @throws IllegalArgumentException 如果 name/value 不符合 Header 规范要求将抛出此异常
         */
        public Builder addHeaderIfNot(String name, String value) {
            Utils.checkHeader(name, value);
            headers.addIfNot(name, value);
            return this;
        }

        /**
         * 清除所有名称为 name 的 Header 参数对
         *
         * @param name 需要清除的 Header 参数对的名称
         */
        public Builder removeHeader(String name) {
            headers.removeAll(name);
            return this;
        }

        /**
         * @return 返回所有已添加的 Header 的名称，顺序与 {@link Builder#headerValues()} 一一对应
         */
        public final List<String> headerNames() {
            return headers.names();
        }

        /**
         * @return 返回所有已添加的 Header 的值，顺序与 {@link Builder#headerNames()} 一一对应
         */
        public final List<String> headerValues() {
            return headers.values();
        }

        /**
         * @return 返回添加的与 name 对应的 value, 如果存在多个则返回先添加的，如果没有则返回 null
         * @throws IllegalArgumentException 如果 name 为 null或空字符串将抛出此异常
         */
        @Nullable
        public final String headerValue(String name) {
            Utils.nonEmpty(name, "name is null/empty");
            return headers.value(name);
        }

        /**
         * @return 返回所有添加的与 name 对应的 value
         * @throws IllegalArgumentException 如果 name 为 null或空字符串将抛出此异常
         */
        @NonNull
        public final List<String> headerValues(String name) {
            Utils.nonEmpty(name, "name is null/empty");
            return headers.values(name);
        }

        /**
         * 清除所有已添加的 Header 参数
         */
        public Builder clearHeaders() {
            headers.clear();
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }
}