package cc.colorcat.netbird2;

import android.support.annotation.NonNull;
import android.util.Log;

import org.junit.Test;

import java.io.IOError;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
        Set<String> set = new HashSet<>();
        for (int i = 0; i < 100000; i++) {
            String s = UUID.randomUUID().toString();
            System.out.println(s);
            assertEquals(set.add(s), true);
        }
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
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("onResponse() " + "request = " + call.request() + "response = " + response);
                System.out.println(response.body().string());
            }

            @Override
            public void onFailure(Call call, StateIOException e) {
                System.out.println("onFailure() " + "\nrequest = " + call.request() + "\n Exception = " + e);
            }
        });
    }

    private static Request.Builder<String> getBuilder() {
        return new Request.Builder<>(StringParser.getUtf8())
                .method(Method.GET)
                .path("/teacher")
                .add("type", Integer.toString(4))
                .add("num", Integer.toString(30));
    }
}