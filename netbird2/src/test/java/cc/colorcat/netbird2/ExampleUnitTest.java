package cc.colorcat.netbird2;

import org.junit.Test;

import java.io.IOError;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    private static NetBird netBird = new NetBird.Builder("http://www.imooc.com/api").build();

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