package saka1029.iterable;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static saka1029.iterable.Iterables.*;
import org.junit.Test;

public class TestCombination {

    @Test
    public void testCount() {
        assertEquals(1, Combination.count(0, 0));
        assertEquals(0, Combination.count(0, 1));
        assertEquals(0, Combination.count(0, 2));
        assertEquals(0, Combination.count(0, 3));
        assertEquals(1, Combination.count(1, 0));
        assertEquals(1, Combination.count(1, 1));
        assertEquals(0, Combination.count(1, 2));
        assertEquals(0, Combination.count(1, 3));
        assertEquals(1, Combination.count(2, 0));
        assertEquals(2, Combination.count(2, 1));
        assertEquals(1, Combination.count(2, 2));
        assertEquals(0, Combination.count(2, 3));
        assertEquals(1, Combination.count(3, 0));
        assertEquals(3, Combination.count(3, 1));
        assertEquals(3, Combination.count(3, 2));
        assertEquals(1, Combination.count(3, 3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCountIllegalN() {
        Combination.count(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCountIllegalK() {
        Combination.count(0, -1);
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

    @Test
    public void testIterable() {
        assertArrayEquals(COMB_0_0, array(Combination.iterable(0, 0)));
        assertArrayEquals(COMB_0_1, array(Combination.iterable(0, 1)));
        assertArrayEquals(COMB_0_2, array(Combination.iterable(0, 2)));
        assertArrayEquals(COMB_0_3, array(Combination.iterable(0, 3)));
        assertArrayEquals(COMB_1_0, array(Combination.iterable(1, 0)));
        assertArrayEquals(COMB_1_1, array(Combination.iterable(1, 1)));
        assertArrayEquals(COMB_1_2, array(Combination.iterable(1, 2)));
        assertArrayEquals(COMB_1_3, array(Combination.iterable(1, 3)));
        assertArrayEquals(COMB_2_0, array(Combination.iterable(2, 0)));
        assertArrayEquals(COMB_2_1, array(Combination.iterable(2, 1)));
        assertArrayEquals(COMB_2_2, array(Combination.iterable(2, 2)));
        assertArrayEquals(COMB_2_3, array(Combination.iterable(2, 3)));
        assertArrayEquals(COMB_3_0, array(Combination.iterable(3, 0)));
        assertArrayEquals(COMB_3_1, array(Combination.iterable(3, 1)));
        assertArrayEquals(COMB_3_2, array(Combination.iterable(3, 2)));
        assertArrayEquals(COMB_3_3, array(Combination.iterable(3, 3)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIterableIllgalN() {
        Combination.iterable(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIterableIllgalK() {
        Combination.iterable(0, -1);
    }

    @Test
    public void testIterableIntArray() {
        assertArrayEquals(new int[][] {{10}, {20}},
            array(Combination.iterable(array(10, 20), 1)));
    }

    @Test
    public void testIterableList() {
        assertEquals(list(list("a"), list("b")),
            list(Combination.iterable(list("a", "b"), 1)));
    }
}