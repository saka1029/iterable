package saka1029.iterable;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static saka1029.iterable.Iterables.*;
import static saka1029.iterable.Combination.*;
import org.junit.Test;

public class TestCombination {

    @Test
    public void testCount() {
        assertEquals(1, count(0, 0));
        assertEquals(0, count(0, 1));
        assertEquals(0, count(0, 2));
        assertEquals(0, count(0, 3));
        assertEquals(1, count(1, 0));
        assertEquals(1, count(1, 1));
        assertEquals(0, count(1, 2));
        assertEquals(0, count(1, 3));
        assertEquals(1, count(2, 0));
        assertEquals(2, count(2, 1));
        assertEquals(1, count(2, 2));
        assertEquals(0, count(2, 3));
        assertEquals(1, count(3, 0));
        assertEquals(3, count(3, 1));
        assertEquals(3, count(3, 2));
        assertEquals(1, count(3, 3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCountIllegalN() {
        count(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCountIllegalK() {
        count(0, -1);
    }

    static final int[][] COMB_0_0 = {{}};
    static final int[][] COMB_0_1 = {};
    static final int[][] COMB_0_2 = {};
    static final int[][] COMB_0_3 = {};
    static final int[][] COMB_1_0 = {{}};
    static final int[][] COMB_1_1 = {{0}};
    static final int[][] COMB_1_2 = {};
    static final int[][] COMB_1_3 = {};
    static final int[][] COMB_2_0 = {{}};
    static final int[][] COMB_2_1 = {{0}, {1}};
    static final int[][] COMB_2_2 = {{0, 1}};
    static final int[][] COMB_2_3 = {};
    static final int[][] COMB_3_0 = {{}};
    static final int[][] COMB_3_1 = {{0}, {1}, {2}};
    static final int[][] COMB_3_2 = {{0, 1}, {0, 2}, {1, 2}};
    static final int[][] COMB_3_3 = {{0, 1, 2}};

    static int[][] toIntArray2D(Iterable<int[]> source) {
        return stream(source).toArray(int[][]::new);
    }

    @Test
    public void testIterable() {
        assertArrayEquals(COMB_0_0, toIntArray2D(iterable(0, 0)));
        assertArrayEquals(COMB_0_1, toIntArray2D(iterable(0, 1)));
        assertArrayEquals(COMB_0_2, toIntArray2D(iterable(0, 2)));
        assertArrayEquals(COMB_0_3, toIntArray2D(iterable(0, 3)));
        assertArrayEquals(COMB_1_0, toIntArray2D(iterable(1, 0)));
        assertArrayEquals(COMB_1_1, toIntArray2D(iterable(1, 1)));
        assertArrayEquals(COMB_1_2, toIntArray2D(iterable(1, 2)));
        assertArrayEquals(COMB_1_3, toIntArray2D(iterable(1, 3)));
        assertArrayEquals(COMB_2_0, toIntArray2D(iterable(2, 0)));
        assertArrayEquals(COMB_2_1, toIntArray2D(iterable(2, 1)));
        assertArrayEquals(COMB_2_2, toIntArray2D(iterable(2, 2)));
        assertArrayEquals(COMB_2_3, toIntArray2D(iterable(2, 3)));
        assertArrayEquals(COMB_3_0, toIntArray2D(iterable(3, 0)));
        assertArrayEquals(COMB_3_1, toIntArray2D(iterable(3, 1)));
        assertArrayEquals(COMB_3_2, toIntArray2D(iterable(3, 2)));
        assertArrayEquals(COMB_3_3, toIntArray2D(iterable(3, 3)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIterableIllgalN() {
        iterable(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIterableIllgalK() {
        iterable(0, -1);
    }

    @Test
    public void testIterableIntArray() {
        assertArrayEquals(new int[][] {{10}, {20}}, stream(iterable(new int[] {10, 20}, 1)).toArray(int[][]::new));
    }

    @Test
    public void testIterableList() {
        assertEquals(list(list("a"), list("b")), list(iterable(list("a", "b"), 1)));
    }
}
