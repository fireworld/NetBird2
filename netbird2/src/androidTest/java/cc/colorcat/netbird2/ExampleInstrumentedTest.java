package cc.colorcat.netbird2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

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
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("cc.colorcat.netbird2.test", appContext.getPackageName());
    }

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
        System.out.println(netBird.execute(getBuilder().build()));
    }

    @Test
    public void netBirdSyncTest2() throws IOException {
        Response response = netBird.newCall(getBuilder().build()).execute();
        System.out.println(response);
        System.out.println(response.body().string());
    }

    @Test
    public void netBirdAsyncTest() {
        netBird.newCall(getBuilder().build()).enqueue(new Callback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("onResponse() " + "request = " + call.request() + "response = " + response);
                System.out.println(response.body().string());
            }

            @Override
            public void onFailure(Call call, StateIOException e) {
                System.out.println("onFailure() " + "\nrequest = " + call.request() + "\n Exception = " + e);
            }

            @Override
            public void onFinish() {

            }
        });
    }

    private static MRequest.Builder<String> getBuilder() {
        return new MRequest.Builder<>(StringParser.getUtf8())
                .method(Method.GET)
                .path("/teacher")
                .add("type", Integer.toString(4))
                .add("num", Integer.toString(30));
    }
}
