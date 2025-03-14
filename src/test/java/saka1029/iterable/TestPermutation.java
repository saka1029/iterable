package saka1029.iterable;

import static saka1029.iterable.Iterables.*;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TestPermutation {

    @Test
    public void testCount() {
        assertEquals(1, permutationCount(0, 0));
        assertEquals(0, permutationCount(0, 1));
        assertEquals(0, permutationCount(0, 2));
        assertEquals(0, permutationCount(0, 3));
        assertEquals(1, permutationCount(1, 0));
        assertEquals(1, permutationCount(1, 1));
        assertEquals(0, permutationCount(1, 2));
        assertEquals(0, permutationCount(1, 3));
        assertEquals(1, permutationCount(2, 0));
        assertEquals(2, permutationCount(2, 1));
        assertEquals(2, permutationCount(2, 2));
        assertEquals(0, permutationCount(2, 3));
        assertEquals(1, permutationCount(3, 0));
        assertEquals(3, permutationCount(3, 1));
        assertEquals(6, permutationCount(3, 2));
        assertEquals(6, permutationCount(3, 3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testpermutationCountIllegalN() {
        permutationCount(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testpermutationCountIllegalK() {
        permutationCount(0, -1);
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
    static final int[][] PERM_3_2 = {
        {0, 1}, {0, 2},
        {1, 0}, {1, 2}, {2, 0}, {2, 1}};
    static final int[][] PERM_3_3 = {
        {0, 1, 2}, {0, 2, 1},
        {1, 0, 2}, {1, 2, 0},
        {2, 0, 1}, {2, 1, 0}};
    static final int[][] PERM_4_0 = {{}};
    static final int[][] PERM_4_1 = {{0}, {1}, {2}, {3}};
    static final int[][] PERM_4_2 = {
        {0, 1}, {0, 2}, {0, 3},
        {1, 0}, {1, 2}, {1, 3},
        {2, 0}, {2, 1}, {2, 3},
        {3, 0}, {3, 1}, {3, 2}};
    static final int[][] PERM_4_3 = {
        {0, 1, 2}, {0, 1, 3}, {0, 2, 1}, {0, 2, 3}, {0, 3, 1}, {0, 3, 2},
        {1, 0, 2}, {1, 0, 3}, {1, 2, 0}, {1, 2, 3}, {1, 3, 0}, {1, 3, 2},
        {2, 0, 1}, {2, 0, 3}, {2, 1, 0}, {2, 1, 3}, {2, 3, 0}, {2, 3, 1},
        {3, 0, 1}, {3, 0, 2}, {3, 1, 0}, {3, 1, 2}, {3, 2, 0}, {3, 2, 1}};
    static final int[][] PERM_4_4 = {
        {0, 1, 2, 3}, {0, 1, 3, 2}, {0, 2, 1, 3}, {0, 2, 3, 1}, {0, 3, 1, 2}, {0, 3, 2, 1},
        {1, 0, 2, 3}, {1, 0, 3, 2}, {1, 2, 0, 3}, {1, 2, 3, 0}, {1, 3, 0, 2}, {1, 3, 2, 0},
        {2, 0, 1, 3}, {2, 0, 3, 1}, {2, 1, 0, 3}, {2, 1, 3, 0}, {2, 3, 0, 1}, {2, 3, 1, 0},
        {3, 0, 1, 2}, {3, 0, 2, 1}, {3, 1, 0, 2}, {3, 1, 2, 0}, {3, 2, 0, 1}, {3, 2, 1, 0}};

    @Test
    public void testIterable() {
        assertArrayEquals(PERM_0_0, int2dArray(permutation(0, 0)));
        assertArrayEquals(PERM_0_1, int2dArray(permutation(0, 1)));
        assertArrayEquals(PERM_0_2, int2dArray(permutation(0, 2)));
        assertArrayEquals(PERM_0_3, int2dArray(permutation(0, 3)));
        assertArrayEquals(PERM_1_0, int2dArray(permutation(1, 0)));
        assertArrayEquals(PERM_1_1, int2dArray(permutation(1, 1)));
        assertArrayEquals(PERM_1_2, int2dArray(permutation(1, 2)));
        assertArrayEquals(PERM_1_3, int2dArray(permutation(1, 3)));
        assertArrayEquals(PERM_2_0, int2dArray(permutation(2, 0)));
        assertArrayEquals(PERM_2_1, int2dArray(permutation(2, 1)));
        assertArrayEquals(PERM_2_2, int2dArray(permutation(2, 2)));
        assertArrayEquals(PERM_2_3, int2dArray(permutation(2, 3)));
        assertArrayEquals(PERM_3_0, int2dArray(permutation(3, 0)));
        assertArrayEquals(PERM_3_1, int2dArray(permutation(3, 1)));
        assertArrayEquals(PERM_3_2, int2dArray(permutation(3, 2)));
        assertArrayEquals(PERM_3_3, int2dArray(permutation(3, 3)));
        assertArrayEquals(PERM_4_0, int2dArray(permutation(4, 0)));
        assertArrayEquals(PERM_4_1, int2dArray(permutation(4, 1)));
        assertArrayEquals(PERM_4_2, int2dArray(permutation(4, 2)));
        assertArrayEquals(PERM_4_3, int2dArray(permutation(4, 3)));
        assertArrayEquals(PERM_4_4, int2dArray(permutation(4, 4)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testpermutationIllgalN() {
        permutation(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testpermutationIllgalK() {
        permutation(0, -1);
    }

    @Test
    public void testPermutationList() {
        assertEquals(listOf(listOf("a", "b"), listOf("b", "a")),
            list(permutation(listOf("a", "b"), 2)));
    }

    static int number(int... digits) {
        return reduce(0, (a, b) -> 10 * a + b, intListOf(digits));
    }

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
                        permutation(10, 8)))));
    }

    /**
     * 
     * <code><pre>
     * problem SendMoreMoney
     * variable 1 9 s m
     * variable 0 9 e n d o r y
     * allDifferent s e n d m o r y
     * constraint n(s, e, n, d) + n(m, o, r, e) == n(m, o, n, e, y)
     * 
     * static int n(int... digits) {
     *     int n = 0;
     *     for (int i : digits)
     *         n = n * 10 + i;
     *     return n;
     * }
     * </pre></code>
     */
    static int solve() {
        int count = 0;
        System.out.println("s,m,e,n,d,o,r,y");
        for (int s = 1; s <= 9; ++s)
        for (int m = 1; m <= 9; ++m)
        if (s != m)
        for (int e = 0; e <= 9; ++e)
        if (s != e && e != m)
        for (int n = 0; n <= 9; ++n)
        if (s != n && n != m && e != n)
        for (int d = 0; d <= 9; ++d)
        if (s != d && n != d && e != d && d != m)
        for (int o = 0; o <= 9; ++o)
        if (m != o && n != o && e != o && d != o && s != o)
        for (int r = 0; r <= 9; ++r)
        if (d != r && n != r && o != r && s != r && m != r && e != r)
        for (int y = 0; y <= 9; ++y)
        if (n != y && s != y && o != y && n(s, e, n, d) + n(m, o, r, e) == n(m, o, n, e, y) && m != y && r != y && d != y && e != y)
        {
            ++count;
            System.out.printf("%d,%d,%d,%d,%d,%d,%d,%d%n", s, m, e, n, d, o, r, y);
            assertEquals(9, s);
            assertEquals(1, m);
            assertEquals(5, e);
            assertEquals(6, n);
            assertEquals(7, d);
            assertEquals(0, o);
            assertEquals(8, r);
            assertEquals(2, y);
        }
        return count;
    }

    static int n(int... digits) {
        int n = 0;
        for (int i : digits)
            n = n * 10 + i;
        return n;
    }

    @Test
    public void testSendMoreMoneyCSP() {
        long start = System.currentTimeMillis();
        int count = solve();
        System.err.printf("solutions: " + count + ", elapse: %d msec.%n", System.currentTimeMillis() - start);
    }

    @Test
    public void testSendMoreMoney() {
        int[] ans = null;
        for (int[] a : permutation(10, 8))
            if (a[0] != 0 && a[4] != 0
                && n(a[0], a[1], a[2], a[3])
                + n(a[4], a[5], a[6], a[1])
                == n(a[4], a[5], a[2], a[1], a[7]))
                ans = a;
        assertArrayEquals(new int[] {9, 5, 6, 7, 1, 0, 8, 2}, ans);
    }

    /**
     * YouTube: "Scary" homework problem is impossibly hard
     * https://www.youtube.com/watch?v=npyvj3klg38
     */
    @Test
    public void test10Digits() {
        for (int[] a : permutation(10, 10))
            if (a[0] + a[1] - a[2] == a[3]
                && a[4] - a[5] == a[6]
                && a[7] + a[8] == a[9])
                System.out.println(Arrays.toString(a));
    }
}
