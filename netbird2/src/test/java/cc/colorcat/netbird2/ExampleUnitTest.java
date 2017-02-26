package cc.colorcat.netbird2;

import org.junit.Test;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

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
        Set<String> waiting = new CopyOnWriteArraySet<>();
//        Queue<String> waiting = new ConcurrentLinkedQueue<>();

        waiting.add("aaa");
        waiting.add("bbb");
        waiting.add("aBc");
        waiting.add("abc");
        waiting.add("ABc");
        System.out.println(waiting);

//        for (String s : waiting) {
//            if (s.equalsIgnoreCase("ABC")) {
//                waiting.remove(s);
//            }
//        }
        Iterator<String> iterator = waiting.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.equalsIgnoreCase("ABC")) {
                waiting.remove(next);
            }
        }
        System.out.println(waiting);
    }
}