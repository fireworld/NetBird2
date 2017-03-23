package cc.colorcat.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import cc.colorcat.netbird2.BitmapParser;
import cc.colorcat.netbird2.FileBody;
import cc.colorcat.netbird2.Headers;
import cc.colorcat.netbird2.MRequest;
import cc.colorcat.netbird2.ProgressInputStream;
import cc.colorcat.netbird2.Interceptor;
import cc.colorcat.netbird2.LoadListener;
import cc.colorcat.netbird2.Method;
import cc.colorcat.netbird2.NetBird;
import cc.colorcat.netbird2.Parameters;
import cc.colorcat.netbird2.Request;
import cc.colorcat.netbird2.Response;
import cc.colorcat.netbird2.ResponseBody;


/**
 * Created by cxx on 2016/12/21.
 * xx.ch@outlook.com
 */

public class ApiService {
    public static final String TAG = "ApiService";

    private static NetBird bird;
    private static final String baseUrl = "http://www.imooc.com/api";
    private static final Interceptor progressListener;

    static {
        progressListener = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                ResponseBody body = response.body();
                if (body != null) {
                    response = response.newBuilder().body(new FakeResponseBody(body, chain.request().loadListener())).build();
                }
                return response;
            }
        };
    }

    public static void init(Context ctx) {
        bird = new NetBird.Builder(baseUrl)
//                .addHeadInterceptor(new TestInterceptorA())
//                .addHeadInterceptor(new TestInterceptorB())
                .enableExceptionLog(BuildConfig.DEBUG)
                .addTailInterceptor(new LogInterceptor())
                .build();
    }

    public static Object send(MRequest<?> req) {
        NetBird netBird = bird;
        return netBird.send(req);
    }

    public static void cancel(Object tag) {
        bird.cancelAll(tag);
    }

    public static void cancelAll() {
        bird.cancelAll();
    }

    public static void cancelWait(Object tag) {
        bird.cancelWaiting(tag);
    }

    public static Object display(final ImageView view, final String url) {
        MRequest<Bitmap> req = new MRequest.Builder<>(BitmapParser.get())
                .url(url)
                .listener(new MRequest.SimpleListener<Bitmap>() {
                    @Override
                    public void onStart() {
                        LogUtils.i("NetBirdImage_start", url);
                    }

                    @Override
                    public void onSuccess(@NonNull Bitmap result) {
                        LogUtils.i("NetBirdImage_success", url);
                        view.setImageBitmap(result);
                    }

                    @Override
                    public void onFailure(int code, @NonNull String msg) {
//                        LogUtils.i("NetBirdImage_fail", view.toString() + " = " + url);
                        LogUtils.e("NetBirdImage_fail", code + ", " + msg);
                    }

                    @Override
                    public void onFinish() {
                        LogUtils.i("NetBirdImage_finish", url);
                    }
                })
                .build();
        return send(req);
    }

    private static class TestInterceptorA implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request req = chain.request().newBuilder()
                    .add("TestA_K1", "TestA_V1")
                    .add("TestA_K2", "TestA_V2")
                    .addIfNot("TestA_K1", "TestA_V1111")
                    .addHeader("TestA_HK1", "TestA_HV1")
                    .addHeader("TestA_HK2", "TestA_HV2")
                    .build();
            return chain.proceed(req);
        }
    }

    private static class TestInterceptorB implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request req = chain.request().newBuilder()
                    .add("TestB_K1", "TestB_V1")
                    .add("TestB_K2", "TestB_V2")
                    .addIfNot("TestB_K1", "TestB_V1111")
                    .addHeader("TestB_HK1", "TestB_HV1")
                    .addHeader("TestB_HK2", "TestB_HV2")
                    .build();
            return chain.proceed(req);
        }
    }

    private static class LogInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request req = chain.request();
            if (LogUtils.isDebug) {
                Method m = req.method();
                LogUtils.ii(TAG, "---------------------------------------- " + m.name() + " -----------------------------------------");
                String url = req.url();
                if (m == Method.GET) {
                    LogUtils.dd(TAG, "req url --> " + url);
                } else {
                    LogUtils.dd(TAG, "req url --> " + url);
                    Parameters parameters = req.parameters();
                    logPairs(parameters.names(), parameters.values(), "parameter");
                    logFiles(req);
                }
                Headers headers = req.headers();
                logPairs(headers.names(), headers.values(), "header");
                LogUtils.ii(TAG, "--------------------------------------------------------------------------------------");
            }
            Response response = chain.proceed(req);
            if (LogUtils.isDebug) {
                LogUtils.ii(TAG, "-------------------------------------- response --------------------------------------");
                LogUtils.ii(TAG, "response --> " + response.code() + "--" + response.msg());
                Headers headers = response.headers();
                for (int i = 0, size = headers.size(); i < size; i++) {
                    String name = headers.name(i);
                    String value = headers.value(i);
                    LogUtils.ii(TAG, "response header --> " + name + " = " + value);
                }
                LogUtils.ii(TAG, "--------------------------------------------------------------------------------------");
            }
            return response;
        }
    }

    private static void logPairs(List<String> names, List<String> values, String mark) {
        for (int i = 0, size = names.size(); i < size; i++) {
            LogUtils.dd(TAG, "req " + mark + " -- > " + names.get(i) + " = " + values.get(i));
        }
    }

    private static void logFiles(Request req) {
        List<FileBody> fileBodies = req.files();
        for (int i = 0, size = fileBodies.size(); i < size; i++) {
            FileBody pack = fileBodies.get(i);
            LogUtils.dd(TAG, "req pack --> " + pack.name() + "--" + pack.contentType() + "--" + pack.file().getAbsolutePath());
        }
    }

    private static class FakeResponseBody extends ResponseBody {
        private ResponseBody body;

        public FakeResponseBody(ResponseBody body, LoadListener listener) {
            InputStream is = body.stream();
            long contentLength = body.contentLength();
            if (contentLength != -1L) {
                is = ProgressInputStream.wrap(is, contentLength, listener);
            }
            this.body = ResponseBody.create(is, contentLength, body.contentType(), body.charset());
        }

        @Override
        public long contentLength() {
            return body.contentLength();
        }

        @Override
        public String contentType() {
            return body.contentType();
        }

        @Override
        public Charset charset() {
            return body.charset();
        }

        @Override
        public InputStream stream() {
            return body.stream();
        }
    }
}
