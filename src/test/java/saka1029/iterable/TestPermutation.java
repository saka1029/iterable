package saka1029.iterable;

import static saka1029.iterable.Iterables.*;
import java.util.stream.IntStream;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TestPermutation {

    @Test
    public void testCount() {
        assertEquals(1, Permutation.count(0, 0));
        assertEquals(0, Permutation.count(0, 1));
        assertEquals(0, Permutation.count(0, 2));
        assertEquals(0, Permutation.count(0, 3));
        assertEquals(1, Permutation.count(1, 0));
        assertEquals(1, Permutation.count(1, 1));
        assertEquals(0, Permutation.count(1, 2));
        assertEquals(0, Permutation.count(1, 3));
        assertEquals(1, Permutation.count(2, 0));
        assertEquals(2, Permutation.count(2, 1));
        assertEquals(2, Permutation.count(2, 2));
        assertEquals(0, Permutation.count(2, 3));
        assertEquals(1, Permutation.count(3, 0));
        assertEquals(3, Permutation.count(3, 1));
        assertEquals(6, Permutation.count(3, 2));
        assertEquals(6, Permutation.count(3, 3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCountIllegalN() {
        Permutation.count(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCountIllegalK() {
        Permutation.count(0, -1);
    }

    static final int[][] PERM_0_0 = {{}};
    static final int[][] PERM_0_1 = {};
    static final int[][] PERM_0_2 = {};
    static final int[][] PERM_0_3 = {};
    static final int[][] PERM_1_0 = {{}};
    static final int[][] PERM_1_1 = {{0}};
    static final int[][] PERM_1_2 = {};
    static final int[][] PERM_1_3 = {};
    static final int[][] PERM_2_0 = {{}};
    static final int[][] PERM_2_1 = {{0}, {1}};
    static final int[][] PERM_2_2 = {{0, 1}, {1, 0}};
    static final int[][] PERM_2_3 = {};
    static final int[][] PERM_3_0 = {{}};
    static final int[][] PERM_3_1 = {{0}, {1}, {2}};
    static final int[][] PERM_3_2 = {{0, 1}, {0, 2}, {1, 0}, {1, 2}, {2, 0}, {2, 1}};
    static final int[][] PERM_3_3 = {{0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}, {2, 0, 1}, {2, 1, 0}};

    @Test
    public void testIterable() {
        assertArrayEquals(PERM_0_0, array(Permutation.iterable(0, 0)));
        assertArrayEquals(PERM_0_1, array(Permutation.iterable(0, 1)));
        assertArrayEquals(PERM_0_2, array(Permutation.iterable(0, 2)));
        assertArrayEquals(PERM_0_3, array(Permutation.iterable(0, 3)));
        assertArrayEquals(PERM_1_0, array(Permutation.iterable(1, 0)));
        assertArrayEquals(PERM_1_1, array(Permutation.iterable(1, 1)));
        assertArrayEquals(PERM_1_2, array(Permutation.iterable(1, 2)));
        assertArrayEquals(PERM_1_3, array(Permutation.iterable(1, 3)));
        assertArrayEquals(PERM_2_0, array(Permutation.iterable(2, 0)));
        assertArrayEquals(PERM_2_1, array(Permutation.iterable(2, 1)));
        assertArrayEquals(PERM_2_2, array(Permutation.iterable(2, 2)));
        assertArrayEquals(PERM_2_3, array(Permutation.iterable(2, 3)));
        assertArrayEquals(PERM_3_0, array(Permutation.iterable(3, 0)));
        assertArrayEquals(PERM_3_1, array(Permutation.iterable(3, 1)));
        assertArrayEquals(PERM_3_2, array(Permutation.iterable(3, 2)));
        assertArrayEquals(PERM_3_3, array(Permutation.iterable(3, 3)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIterableIllgalN() {
        Permutation.iterable(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIterableIllgalK() {
        Permutation.iterable(0, -1);
    }

    @Test
    public void testIterableIntArray() {
        assertArrayEquals(new int[][] {{10, 20}, {20, 10}},
            array(Permutation.iterable(arrayOf(10, 20), 2)));
    }

    @Test
    public void testIterableList() {
        assertEquals(listOf(listOf("a", "b"), listOf("b", "a")),
            list(Permutation.iterable(listOf("a", "b"), 2)));
    }

    static int number(int... digits) {
        return reduce(0, (a, b) -> 10 * a + b, intListOf(digits));
    }

    /**
     * <pre>
     * 0 1 2 3 4 5 6 7
     * S E N D M O R Y
     * </pre>
     */
    static boolean isSendMoreMoney(int s, int e, int n, int d, int m, int o, int r, int y) {
        return s != 0 && m != 0
            && number(s, e, n, d) + number(m, o, r, e) == number(m, o, n, e, y);
    }

    @Test
    public void TestSendMoreMoney() {
        assertEquals(listOf(listOf(9, 5, 6, 7, 1, 0, 8, 2)),
            list(
                map(ints -> intListOf(ints),
                    filter(d -> isSendMoreMoney(d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7]),
                        Permutation.iterable(10, 8)))));
    }
}
