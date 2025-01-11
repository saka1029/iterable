package saka1029.iterable;

import static org.junit.Assert.assertEquals;
import static saka1029.iterable.Iterables.*;
import org.junit.Test;

public class TestGenerator {

    @Test
    public void testGenerator() {
        Generator<Integer> g = new Generator<>(q -> {
            q.yield(1);
            q.yield(0);
            q.yield(3);
        });
        assertEquals(listOf(1, 0, 3), list(g));
        assertEquals(listOf(1, 0, 3), list(g));
    }

    @Test
    public void testFibonacci() {
        Generator<Integer> fibonacci = new Generator<>(g -> {
            int a = 0, b = 1;
            while (true) {
                g.yield(a);
                int c = a + b;
                a = b;
                b = c;
            }
        });
        assertEquals(listOf(0, 1, 1, 2, 3, 5, 8, 13), list(limit(8, fibonacci)));
    }

}
