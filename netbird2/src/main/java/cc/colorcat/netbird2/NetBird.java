package cc.colorcat.netbird2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cc.colorcat.netbird2.request.Request;
import cc.colorcat.netbird2.util.Utils;

/**
 * Created by cxx on 17-2-22.
 * xx.ch@outlook.com
 */
public final class NetBird implements Call.Factory {
    private final List<Interceptor> headInterceptors;
    private final List<Interceptor> tailInterceptors;
    private final ExecutorService executor;
    private final Dispatcher dispatcher;
    private final Connection connection;
    private final String baseUrl;
    private final long cacheSize;
    private final File cachePath;
    private final int maxRunning;
    private final int readTimeOut;
    private final int connectTimeOut;

    private NetBird(Builder builder) {
        this.headInterceptors = Utils.immutableList(builder.headInterceptors);
        this.tailInterceptors = Utils.immutableList(builder.tailInterceptors);
        this.executor = builder.executor;
        this.dispatcher = new Dispatcher(this);
        this.connection = builder.connection;
        this.baseUrl = builder.baseUrl;
        this.cacheSize = builder.cacheSize;
        this.cachePath = builder.cachePath;
        this.maxRunning = builder.maxRunning;
        this.readTimeOut = builder.readTimeOut;
        this.connectTimeOut = builder.connectTimeOut;
    }

    public List<Interceptor> headInterceptors() {
        return headInterceptors;
    }

    public List<Interceptor> tailInterceptors() {
        return tailInterceptors;
    }

    public ExecutorService executor() {
        return executor;
    }

    public Dispatcher dispatcher() {
        return dispatcher;
    }

    public Connection connection() {
        return connection;
    }

    public String baseUrl() {
        return baseUrl;
    }

    public long cacheSize() {
        return cacheSize;
    }

    public File cachePath() {
        return cachePath;
    }

    public int maxRunning() {
        return maxRunning;
    }

    public int readTimeOut() {
        return readTimeOut;
    }

    public int connectTimeOut() {
        return connectTimeOut;
    }

    @Override
    public Call newCall(Request<?> request) {
        return new RealCall(this, request);
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    private static class CallFactory implements Call.Factory {
        @Override
        public Call newCall(Request<?> request) {
            return null;
        }
    }

    public static final class Builder {
        private List<Interceptor> headInterceptors;
        private List<Interceptor> tailInterceptors;
        private ExecutorService executor;
        private Connection connection;
        private String baseUrl;
        private long cacheSize;
        private File cachePath;
        private int maxRunning = 6;
        private int readTimeOut = 10000;
        private int connectTimeOut = 10000;

        public Builder(String baseUrl) {
            this.baseUrl = Utils.checkedHttp(baseUrl);
            this.headInterceptors = new ArrayList<>(2);
            this.tailInterceptors = new ArrayList<>(2);
        }

        private Builder(NetBird netBird) {
            this.baseUrl = netBird.baseUrl;
            this.headInterceptors = new ArrayList<>(netBird.headInterceptors);
            this.tailInterceptors = new ArrayList<>(netBird.tailInterceptors);
            this.executor = netBird.executor;
            this.connection = netBird.connection;
            this.cacheSize = netBird.cacheSize;
            this.cachePath = netBird.cachePath;
            this.maxRunning = netBird.maxRunning;
            this.readTimeOut = netBird.readTimeOut;
            this.connectTimeOut = netBird.connectTimeOut;
        }

        public Builder executor(ExecutorService executor) {
            this.executor = Utils.nonNull(executor, "executor == null");
            return this;
        }

        public Builder connection(Connection connection) {
            this.connection = Utils.nonNull(connection, "connection == null");
            return this;
        }

        public Builder addHeadInterceptor(Interceptor interceptor) {
            headInterceptors.add(Utils.nonNull(interceptor, "interceptor == null"));
            return this;
        }

        public Builder addTailInterceptor(Interceptor interceptor) {
            tailInterceptors.add(Utils.nonNull(interceptor, "interceptor == null"));
            return this;
        }

        public Builder cache(File cachePath, long cacheSize) {
            if (cacheSize <= 0L) {
                throw new IllegalArgumentException("cacheSize <= 0");
            }
            this.cachePath = cachePath;
            this.cacheSize = cacheSize;
            return this;
        }

        public Builder maxRunning(int maxRunning) {
            if (maxRunning <= 0) {
                throw new IllegalArgumentException("maxRunning <= 0");
            }
            this.maxRunning = maxRunning;
            return this;
        }

        public Builder readTimeOut(int milliseconds) {
            if (milliseconds <= 0) {
                throw new IllegalArgumentException("readTimeOut <= 0");
            }
            this.readTimeOut = milliseconds;
            return this;
        }

        public Builder connectTimeOut(int milliseconds) {
            if (milliseconds <= 0) {
                throw new IllegalArgumentException("connectTimeOut <= 0");
            }
            this.connectTimeOut = milliseconds;
            return this;
        }

        public NetBird build() {
            if (executor == null) executor = defaultService(maxRunning);
            if (connection == null) connection = new HttpConnection();
            return new NetBird(this);
        }

        private static ExecutorService defaultService(int corePoolSize) {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, 10, 60L, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<Runnable>(), new ThreadPoolExecutor.DiscardOldestPolicy());
            executor.allowCoreThreadTimeOut(true);
            return executor;
        }
    }
}
