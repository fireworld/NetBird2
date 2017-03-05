package cc.colorcat.netbird2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.List;

/**
 * Created by cxx on 17-3-4.
 * xx.ch@outlook.com
 */
public class MRequest<T> extends Request {
    private final Parser<? extends T> parser;
    private final Listener<? super T> listener;

    protected MRequest(Builder<T> builder) {
        super(builder);
        this.parser = builder.parser;
        this.listener = builder.listener;
    }

    public Listener<? super T> listener() {
        return listener;
    }

    public Parser<? extends T> parser() {
        return parser;
    }

    public interface Listener<R> {
        void onStart();

        void onSuccess(R result);

        void onFailure(int code, String msg);

        void onFinish();
    }

    public static abstract class SimpleListener<R> implements Listener<R> {

        @Override
        public void onStart() {

        }

        @Override
        public void onFinish() {

        }
    }


    public static class Builder<T> extends Request.Builder {
        private Parser<? extends T> parser;
        private Listener<? super T> listener;

        protected Builder(MRequest<T> request) {
            super(request);
            this.parser = request.parser;
            this.listener = request.listener;
        }

        public Builder(Parser<? extends T> parser) {
            super();
            this.parser = parser;
        }

        @Override
        public Builder<T> tag(Object tag) {
            super.tag(tag);
            return this;
        }

        @Override
        public Builder<T> url(String url) {
            super.url(url);
            return this;
        }

        @Override
        public Builder<T> path(String path) {
            super.path(path);
            return this;
        }

        @Override
        public Builder<T> method(Method method) {
            super.method(method);
            return this;
        }

        public Builder<T> listener(Listener<? super T> listener) {
            this.listener = listener;
            return this;
        }

        @Override
        public Builder<T> loadListener(LoadListener listener) {
            super.loadListener(listener);
            return this;
        }

        @Override
        public Builder<T> add(String name, String value) {
            super.add(name, value);
            return this;
        }

        @Override
        public Builder<T> set(String name, String value) {
            super.set(name, value);
            return this;
        }

        @Override
        public Builder<T> addIfNot(String name, String value) {
            super.addIfNot(name, value);
            return this;
        }

        @Override
        public Builder<T> remove(String name) {
            super.remove(name);
            return this;
        }

        @Override
        public List<String> names() {
            return super.names();
        }

        @Override
        public List<String> values() {
            return super.values();
        }

        @Nullable
        @Override
        public String value(String name) {
            return super.value(name);
        }

        @Override
        public Builder<T> clear() {
            super.clear();
            return this;
        }

        @Override
        public Builder<T> addFile(String name, String contentType, File file) {
            super.addFile(name, contentType, file);
            return this;
        }

        @Override
        public Builder<T> addFile(String name, String contentType, File file, UploadListener listener) {
            super.addFile(name, contentType, file, listener);
            return this;
        }

        @Override
        public Builder<T> clearFile() {
            super.clearFile();
            return this;
        }

        @Override
        public Builder<T> addHeader(String name, String value) {
            super.addHeader(name, value);
            return this;
        }

        @Override
        public Builder<T> setHeader(String name, String value) {
            super.setHeader(name, value);
            return this;
        }

        @Override
        public Builder<T> addHeaderIfNot(String name, String value) {
            super.addHeaderIfNot(name, value);
            return this;
        }

        @Override
        public Builder<T> removeHeader(String name) {
            super.removeHeader(name);
            return this;
        }

        @Override
        public List<String> headerNames() {
            return super.headerNames();
        }

        @Override
        public List<String> headerValues() {
            return super.headerValues();
        }

        @Nullable
        @Override
        public String headerValue(String name) {
            return super.headerValue(name);
        }

        @NonNull
        @Override
        public List<String> headerValues(String name) {
            return super.headerValues(name);
        }

        @Override
        public Builder<T> clearHeaders() {
            super.clearHeaders();
            return this;
        }

        @Override
        public MRequest<T> build() {
            return new MRequest<>(this);
        }
    }
}