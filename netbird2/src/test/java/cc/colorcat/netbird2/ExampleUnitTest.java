package cc.colorcat.netbird2;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void genericTest() {
    }

    // http://www.imooc.com/api
    private static NetBird netBird = new NetBird.Builder("http://www.imooc.com/api")
            .executor(new ExecutorService() {
                @Override
                public void shutdown() {

                }

                @NonNull
                @Override
                public List<Runnable> shutdownNow() {
                    return null;
                }

                @Override
                public boolean isShutdown() {
                    return false;
                }

                @Override
                public boolean isTerminated() {
                    return false;
                }

                @Override
                public boolean awaitTermination(long timeout, @NonNull TimeUnit unit) throws InterruptedException {
                    return false;
                }

                @NonNull
                @Override
                public <T> Future<T> submit(@NonNull Callable<T> task) {
                    return null;
                }

                @NonNull
                @Override
                public <T> Future<T> submit(@NonNull Runnable task, T result) {
                    return null;
                }

                @NonNull
                @Override
                public Future<?> submit(@NonNull Runnable task) {
                    return null;
                }

                @NonNull
                @Override
                public <T> List<Future<T>> invokeAll(@NonNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
                    return null;
                }

                @NonNull
                @Override
                public <T> List<Future<T>> invokeAll(@NonNull Collection<? extends Callable<T>> tasks, long timeout, @NonNull TimeUnit unit) throws InterruptedException {
                    return null;
                }

                @NonNull
                @Override
                public <T> T invokeAny(@NonNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
                    return null;
                }

                @Override
                public <T> T invokeAny(@NonNull Collection<? extends Callable<T>> tasks, long timeout, @NonNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                    return null;
                }

                @Override
                public void execute(@NonNull Runnable command) {
                    command.run();
                }
            })
            .build();

    @Test
    public void netBirdSyncTest() throws Exception {
        Response response = netBird.newCall(new Request.Builder().url("http://www.google.com").build()).execute();
        ResponseBody body = response.body();
        Headers headers = response.headers();
        if (body != null) {
            System.out.println("body: " + body.string());
        }
        System.out.println("Headers: " + headers);
    }

    private static class Upload implements UploadListener {
        @Override
        public void onChanged(long written, long total, int percent) {
            System.out.println("written = " + written + ", total = " + total + ", percent = " + percent);
        }
    }

    @Test
    public void netBirdSyncTest2() throws IOException {
        Response response = netBird.newCall(getBuilder().build()).execute();
        System.out.println(response);
        System.out.println(response.body().string());
    }

    @Test
    public void netBirdAsyncTest() {
        netBird.newCall(new Request.Builder().url("https://www.baidu.com").build()).enqueue(new Callback() {
            @Override
            public void onStart() {
                System.out.println("onStart()");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("onResponse() " + "request = " + call.request() + "response = " + response);
                if (response.body() != null) {
                    System.out.println(response.body().string());
                }
            }

            @Override
            public void onFailure(Call call, StateIOException e) {
                System.out.println("onFailure() " + "\nrequest = " + call.request() + "\n Exception = " + e);
            }

            @Override
            public void onFinish() {
                System.out.println("onFinish()");
            }
        });
    }

    @Test
    public void netBirdAsyncTest2() {
        netBird.send(getBuilder().listener(new MRequest.Listener<String>() {
            @Override
            public void onStart() {
                System.out.println("onStart()");
            }

            @Override
            public void onSuccess(String result) {
                System.out.println("onSuccess() " + result);
            }

            @Override
            public void onFailure(int code, String msg) {
                System.out.println("onFailure() " + code + ", " + msg);
            }

            @Override
            public void onFinish() {
                System.out.println("onFinish()");
            }
        }).build());
    }

    private static MRequest.Builder<String> getBuilder() {
        return new MRequest.Builder<>(StringParser.getUtf8())
                .method(Method.GET)
                .path("/teacher")
                .add("type", Integer.toString(4))
                .add("num", Integer.toString(30));
    }
}