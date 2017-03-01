package cc.colorcat.netbird2;

import org.junit.Test;

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
}