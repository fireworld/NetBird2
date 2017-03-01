package cc.colorcat.netbird2.request;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.colorcat.netbird2.meta.Headers;
import cc.colorcat.netbird2.meta.Parameters;
import cc.colorcat.netbird2.meta.WritableHeaders;
import cc.colorcat.netbird2.meta.WritableParameters;
import cc.colorcat.netbird2.parser.Parser;
import cc.colorcat.netbird2.response.LoadListener;
import cc.colorcat.netbird2.response.NetworkData;
import cc.colorcat.netbird2.response.Response;
import cc.colorcat.netbird2.util.Utils;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
@SuppressWarnings("unused")
public class Request<T> {
    private Parameters params;
    private Headers headers;
    private String url;
    private String path;
    private Method method;
    private Parser<? extends T> parser;
    private List<FileBody> fileBodies;
    private String boundary;
    private RequestListener<? super T> requestListener;
    private LoadListener loadListener;
    private Object tag;

    protected Request(Builder<T> builder) {
        this.params = builder.params.newReadableParameters();
        this.headers = builder.headers.newReadableHeaders();
        this.url = builder.url;
        this.path = builder.path;
        this.method = builder.method;
        this.parser = builder.parser;
        this.fileBodies = builder.fileBodies != null ? Utils.immutableList(builder.fileBodies) : null;
        this.boundary = builder.boundary;
        this.requestListener = builder.requestListener;
        this.loadListener = builder.loadListener;
        this.tag = builder.tag;
    }

    public Builder<T> newBuilder() {
        return new Builder<>(this);
    }

    public String url() {
        return url;
    }

    public String path() {
        return path;
    }

    public Method method() {
        return method;
    }

    public Parameters parameters() {
        return params;
    }

    public Headers headers() {
        return headers;
    }

    public List<FileBody> files() {
        return Utils.nullElse(fileBodies, Collections.<FileBody>emptyList());
    }

    public LoadListener loadListener() {
        return loadListener;
    }

    public RequestBody body() {
        if (params.isEmpty() && fileBodies == null) {
            return null;
        }
        if (params.isEmpty() && fileBodies != null && fileBodies.size() == 1) {
            return fileBodies.get(0);
        }
        if (!params.isEmpty() && fileBodies == null) {
            return FormBody.create(params);
        }
        return MultipartBody.create(FormBody.create(params), fileBodies, boundary);
    }

    public Object tag() {
        return tag;
    }

    public void onStart() {
        if (requestListener != null) {
            if (Utils.isUiThread()) {
                requestListener.onStart();
            } else {
                Utils.postOnUi(new Runnable() {
                    @Override
                    public void run() {
                        requestListener.onStart();
                    }
                });
            }
        }
    }

    public NetworkData<? extends T> parse(@NonNull Response response) throws IOException {
        return parser.parse(response);
    }

    public void deliver(@NonNull final NetworkData<? extends T> data) {
        if (Utils.isUiThread()) {
            realDeliver(data);
        } else {
            Utils.postOnUi(new Runnable() {
                @Override
                public void run() {
                    realDeliver(data);
                }
            });
        }
    }

    private void realDeliver(final NetworkData<? extends T> data) {
        if (requestListener != null) {
            if (data.isSuccess) {
                requestListener.onSuccess(data.data);
            } else {
                requestListener.onFailure(data.code, data.msg);
            }
            requestListener.onFinish();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request<?> request = (Request<?>) o;

        if (url != null ? !url.equals(request.url) : request.url != null) return false;
        if (path != null ? !path.equals(request.path) : request.path != null) return false;
        if (method != request.method) return false;
        if (!params.equals(request.params)) return false;
        if (!headers.equals(request.headers)) return false;
        return fileBodies != null ? fileBodies.equals(request.fileBodies) : request.fileBodies == null;

    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + method.hashCode();
        result = 31 * result + params.hashCode();
        result = 31 * result + headers.hashCode();
        result = 31 * result + (fileBodies != null ? fileBodies.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Request{" +
                "url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", method=" + method +
                ", params=" + params +
                ", headers=" + headers +
                ", fileBodies=" + fileBodies +
                ", parser=" + parser +
                ", loadListener=" + loadListener +
                ", requestListener=" + requestListener +
                ", tag=" + tag +
                '}';
    }

    public static class Builder<T> {
        private WritableParameters params;
        private WritableHeaders headers;
        private String url;
        private String path;
        private Method method;
        private Parser<? extends T> parser;
        private List<FileBody> fileBodies;
        private String boundary;
        private RequestListener<? super T> requestListener;

        private LoadListener loadListener;

        private Object tag;

        protected Builder(Request<T> req) {
            this.params = req.params.newWritableParameters();
            this.headers = req.headers.newWritableHeaders();
            this.url = req.url;
            this.path = req.path;
            this.method = req.method;
            this.parser = req.parser;
            this.fileBodies = req.fileBodies != null ? new ArrayList<>(req.fileBodies) : null;
            this.boundary = req.boundary;
            this.requestListener = req.requestListener;
            this.loadListener = req.loadListener;
            this.tag = req.tag;
        }

        /**
         * @param parser 数据解析，将 {@link Response} 解析为目标数据
         */
        public Builder(@NonNull Parser<? extends T> parser) {
            this.parser = Utils.nonNull(parser, "parser == null");
            this.params = WritableParameters.create(4);
            this.headers = WritableHeaders.create(2);
            this.method = Method.GET;
            this.boundary = "==" + System.currentTimeMillis() + "==";
        }

        public Builder<T> tag(Object tag) {
            this.tag = tag;
            return this;
        }

        /**
         * @param url 请求的 http/https 地址，如果没有设置则使用构建 NetBird 时的 baseUrl
         */
        public Builder<T> url(String url) {
            this.url = Utils.checkedHttp(url);
            return this;
        }

        /**
         * @param path 请求的路径
         */
        public Builder<T> path(String path) {
            this.path = path;
            return this;
        }

        public Builder<T> method(Method method) {
            this.method = method;
            return this;
        }

        /**
         * @param listener 请求结果的回调，{@link RequestListener} 中的方法均在主线程执行
         */
        public Builder<T> listener(RequestListener<? super T> listener) {
            this.requestListener = listener;
            return this;
        }

        /**
         * @param listener 下载进度监听器，服务器必须返回数据的长度才有效
         */
        public Builder<T> loadListener(LoadListener listener) {
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
        public Builder<T> add(String name, String value) {
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
        public Builder<T> set(String name, String value) {
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
        public Builder<T> addIfNot(String name, String value) {
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
        public Builder<T> remove(String name) {
            Utils.nonEmpty(name, "name is null/empty");
            params.removeAll(name);
            return this;
        }

        /**
         * @return 返回所有请求参数的名称，不可修改，顺序与 {@link Builder#values()} 一一对应。
         */
        public List<String> names() {
            return params.names();
        }

        /**
         * @return 返回所有请求参数的值，不可修改，顺序与 {@link Builder#names()} 一一对应。
         */
        public List<String> values() {
            return params.values();
        }

        /**
         * @return 返回添加的与 name 对应的 value, 如果存在多个则返回先添加的，如果没有则返回 null
         * @throws IllegalArgumentException 如果 name 为 null或空字符串将抛出此异常
         */
        @Nullable
        public String value(String name) {
            Utils.nonEmpty(name, "name is null/empty");
            return params.value(name);
        }

        /**
         * 清除所有已添加的请求参数
         */
        public Builder<T> clear() {
            params.clear();
            return this;
        }

        public Builder<T> addFile(String name, String contentType, File file) {
            return addFile(name, contentType, file, null);
        }

        /**
         * 添加需要上传的文件
         *
         * @param name        参数名
         * @param contentType 文件类型，如 image/png
         * @param file        文件全路径
         * @throws IllegalArgumentException 如果 name/contentType 为 null 或空字符串，或 file 为 null 或不存在，均将抛出此异常。
         */
        public Builder<T> addFile(String name, String contentType, File file, UploadListener listener) {
            if (fileBodies == null) {
                fileBodies = new ArrayList<>(2);
            }
            fileBodies.add(FileBody.create(name, contentType, file, listener));
            return this;
        }

        public Builder<T> clearFile() {
            if (fileBodies != null) {
                fileBodies.clear();
            }
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
        public Builder<T> addHeader(String name, String value) {
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
        public Builder<T> setHeader(String name, String value) {
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
        public Builder<T> addHeaderIfNot(String name, String value) {
            Utils.checkHeader(name, value);
            headers.addIfNot(name, value);
            return this;
        }

        public Builder<T> removeHeader(String name) {
            headers.removeAll(name);
            return this;
        }

        /**
         * @return 返回所有已添加的 Header 的名称，顺序与 {@link Builder#headerValues()} 一一对应
         */
        public List<String> headerNames() {
            return headers.names();
        }

        /**
         * @return 返回所有已添加的 Header 的值，顺序与 {@link Builder#headerNames()} 一一对应
         */
        public List<String> headerValues() {
            return headers.values();
        }

        /**
         * @return 返回添加的与 name 对应的 value, 如果存在多个则返回先添加的，如果没有则返回 null
         * @throws IllegalArgumentException 如果 name 为 null或空字符串将抛出此异常
         */
        @Nullable
        public String headerValue(String name) {
            Utils.nonEmpty(name, "name is null/empty");
            return headers.value(name);
        }

        /**
         * @return 返回所有添加的与 name 对应的 value
         * @throws IllegalArgumentException 如果 name 为 null或空字符串将抛出此异常
         */
        @NonNull
        public List<String> headerValues(String name) {
            Utils.nonEmpty(name, "name is null/empty");
            return headers.values(name);
        }

        /**
         * 清除所有已添加的 Header 参数
         */
        public Builder<T> clearHeaders() {
            headers.clear();
            return this;
        }

        @CallSuper
        public Request<T> build() {
            if (fileBodies != null) method = Method.POST;
            if (tag == null) tag = boundary;
            return new Request<>(this);
        }
    }
}