package saka1029.iterable;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Test;

public class TestStream {

    @Test
    public void testIterateArray() {
        assertArrayEquals(
            new int[] {0, 1, 1, 2, 3, 5, 8, 13, 21, 34},
            Stream.iterate(new int[] {0, 1}, a -> new int[] {a[1], a[0] + a[1]})
                .limit(10)
                .mapToInt(a -> a[0])
                .toArray());
        assertEquals(
            Stream.of(new Integer[] {0, 1, 1, 2, 3, 5, 8, 13, 21, 34})
                .map(BigInteger::valueOf)
                .toList(),
            Stream.iterate(new BigInteger[] {BigInteger.ZERO, BigInteger.ONE},
                    a -> new BigInteger[] {a[1], a[0].add(a[1])})
                .limit(10)
                .map(a -> a[0])
                .toList());
    }

    @Test
    public void testIterateRecord() {
        record A(int a, int b) {}
        assertEquals(List.of(0, 1, 1, 2, 3, 5, 8, 13, 21, 34),
        Stream.iterate(new A(0, 1), a -> new A(a.b, a.a + a.b))
            .limit(10)
            .map(a -> a.a)
            .toList());
    }

    @Test
    public void testIterateObject() {
        assertEquals(List.of(0, 1, 1, 2, 3, 5, 8, 13, 21, 34),
        Stream.iterate(new Object() {int a = 0, b = 1; }, a -> {
                int t = a.a + a.b;
                a.a = a.b;
                a.b = t;
                return a;
            })
            .limit(10)
            .map(a -> a.a)
            .toList());
    }
}
