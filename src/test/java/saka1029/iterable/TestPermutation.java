package saka1029.iterable;

import static saka1029.iterable.Permutation.*;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TestPermutation {

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
        assertEquals(2, count(2, 2));
        assertEquals(0, count(2, 3));
        assertEquals(1, count(3, 0));
        assertEquals(3, count(3, 1));
        assertEquals(6, count(3, 2));
        assertEquals(6, count(3, 3));
    }
}
