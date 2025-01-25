package saka1029.iterable;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static saka1029.iterable.Iterables.*;
import org.junit.Test;

public class TestCombination {

    @Test
    public void testCount() {
        assertEquals(1, combinationCount(0, 0));
        assertEquals(0, combinationCount(0, 1));
        assertEquals(0, combinationCount(0, 2));
        assertEquals(0, combinationCount(0, 3));
        assertEquals(1, combinationCount(1, 0));
        assertEquals(1, combinationCount(1, 1));
        assertEquals(0, combinationCount(1, 2));
        assertEquals(0, combinationCount(1, 3));
        assertEquals(1, combinationCount(2, 0));
        assertEquals(2, combinationCount(2, 1));
        assertEquals(1, combinationCount(2, 2));
        assertEquals(0, combinationCount(2, 3));
        assertEquals(1, combinationCount(3, 0));
        assertEquals(3, combinationCount(3, 1));
        assertEquals(3, combinationCount(3, 2));
        assertEquals(1, combinationCount(3, 3));
        assertEquals(1, combinationCount(4, 0));
        assertEquals(4, combinationCount(4, 1));
        assertEquals(6, combinationCount(4, 2));
        assertEquals(4, combinationCount(4, 3));
        assertEquals(1, combinationCount(4, 4));
        assertEquals(0, combinationCount(4, 5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCountIllegalN() {
        combinationCount(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCountIllegalK() {
        combinationCount(0, -1);
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
    static final int[][] COMB_4_0 = {{}};
    static final int[][] COMB_4_1 = {{0}, {1}, {2}, {3}};
    static final int[][] COMB_4_2 = {{0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 3}, {2, 3}};
    static final int[][] COMB_4_3 = {{0, 1, 2}, {0, 1, 3}, {0, 2, 3}, {1, 2, 3}};
    static final int[][] COMB_4_4 = {{0, 1, 2, 3}};
    static final int[][] COMB_4_5 = {};

    @Test
    public void testIterable() {
        assertArrayEquals(COMB_0_0, int2dArray(combination(0, 0)));
        assertArrayEquals(COMB_0_1, int2dArray(combination(0, 1)));
        assertArrayEquals(COMB_0_2, int2dArray(combination(0, 2)));
        assertArrayEquals(COMB_0_3, int2dArray(combination(0, 3)));
        assertArrayEquals(COMB_1_0, int2dArray(combination(1, 0)));
        assertArrayEquals(COMB_1_1, int2dArray(combination(1, 1)));
        assertArrayEquals(COMB_1_2, int2dArray(combination(1, 2)));
        assertArrayEquals(COMB_1_3, int2dArray(combination(1, 3)));
        assertArrayEquals(COMB_2_0, int2dArray(combination(2, 0)));
        assertArrayEquals(COMB_2_1, int2dArray(combination(2, 1)));
        assertArrayEquals(COMB_2_2, int2dArray(combination(2, 2)));
        assertArrayEquals(COMB_2_3, int2dArray(combination(2, 3)));
        assertArrayEquals(COMB_3_0, int2dArray(combination(3, 0)));
        assertArrayEquals(COMB_3_1, int2dArray(combination(3, 1)));
        assertArrayEquals(COMB_3_2, int2dArray(combination(3, 2)));
        assertArrayEquals(COMB_3_3, int2dArray(combination(3, 3)));
        assertArrayEquals(COMB_4_0, int2dArray(combination(4, 0)));
        assertArrayEquals(COMB_4_1, int2dArray(combination(4, 1)));
        assertArrayEquals(COMB_4_2, int2dArray(combination(4, 2)));
        assertArrayEquals(COMB_4_3, int2dArray(combination(4, 3)));
        assertArrayEquals(COMB_4_4, int2dArray(combination(4, 4)));
        assertArrayEquals(COMB_4_5, int2dArray(combination(4, 5)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIterableIllgalN() {
        combination(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIterableIllgalK() {
        combination(0, -1);
    }

    @Test
    public void testIterableList() {
        assertEquals(listOf(listOf("a"), listOf("b")),
            list(combination(listOf("a", "b"), 1)));
    }
}
