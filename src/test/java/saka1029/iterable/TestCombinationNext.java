package saka1029.iterable;

import static org.junit.Assert.assertArrayEquals;
import static saka1029.iterable.Iterables.*;
import static saka1029.iterable.TestCombination.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;

public class TestCombinationNext {

    static boolean next(int n, int[] s) {
        int r = s.length, nmr = n - r;
        int i;
        for (i = r - 1; i >= 0 && s[i] >= nmr + i; --i)
            /* do nothing */;
        if (i < 0)
            return false;
        int a = ++s[i];
        for (int j = i + 1; j < r; ++j)
            s[j] = ++a;
        return true;
    }

    static int[][] combinations(int n, int r) {
        int[] s = intArray(range(0, r, 1));
        // int[] s = IntStream.range(0, r).toArray();
        List<int[]> result = new ArrayList<>();
        if (n >= r)
            do {
                result.add(s.clone());
            } while (next(n, s));
        return array(map(ints -> intArrayOf(ints), result));
    }

    @Test
    public void testNext() {
        assertArrayEquals(COMB_0_0, combinations(0, 0));
        assertArrayEquals(COMB_0_1, combinations(0, 1));
        assertArrayEquals(COMB_0_2, combinations(0, 2));
        assertArrayEquals(COMB_0_3, combinations(0, 3));
        assertArrayEquals(COMB_1_0, combinations(1, 0));
        assertArrayEquals(COMB_1_1, combinations(1, 1));
        assertArrayEquals(COMB_1_2, combinations(1, 2));
        assertArrayEquals(COMB_1_3, combinations(1, 3));
        assertArrayEquals(COMB_2_0, combinations(2, 0));
        assertArrayEquals(COMB_2_1, combinations(2, 1));
        assertArrayEquals(COMB_2_2, combinations(2, 2));
        assertArrayEquals(COMB_2_3, combinations(2, 3));
        assertArrayEquals(COMB_3_0, combinations(3, 0));
        assertArrayEquals(COMB_3_1, combinations(3, 1));
        assertArrayEquals(COMB_3_2, combinations(3, 2));
        assertArrayEquals(COMB_3_3, combinations(3, 3));
        assertArrayEquals(COMB_4_0, combinations(4, 0));
        assertArrayEquals(COMB_4_1, combinations(4, 1));
        assertArrayEquals(COMB_4_2, combinations(4, 2));
        assertArrayEquals(COMB_4_3, combinations(4, 3));
        assertArrayEquals(COMB_4_4, combinations(4, 4));
        assertArrayEquals(COMB_4_5, combinations(4, 5));
    }

    public static Iterable<int[]> iterable(int n, int k) {
        return () -> new Iterator<>() {
            int nmk = n - k;
            int[] selected = intArray(range(0, k, 1));
            boolean hasNext = nmk >= 0;

            private boolean advance() {
                int i = k - 1;
                while (i >= 0 && selected[i] >= nmk + i)
                    --i;
                if (i < 0)
                    return false;
                int a = ++selected[i];
                for (int j = i + 1; j < k; ++j)
                    selected[j] = ++a;
                return true;
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public int[] next() {
                int[] result = selected.clone();
                hasNext = advance();
                return result;
            }
        };
    }

    @Test
    public void testIterable() {
        assertArrayEquals(COMB_0_0, array(iterable(0, 0)));
        assertArrayEquals(COMB_0_1, array(iterable(0, 1)));
        assertArrayEquals(COMB_0_2, array(iterable(0, 2)));
        assertArrayEquals(COMB_0_3, array(iterable(0, 3)));
        assertArrayEquals(COMB_1_0, array(iterable(1, 0)));
        assertArrayEquals(COMB_1_1, array(iterable(1, 1)));
        assertArrayEquals(COMB_1_2, array(iterable(1, 2)));
        assertArrayEquals(COMB_1_3, array(iterable(1, 3)));
        assertArrayEquals(COMB_2_0, array(iterable(2, 0)));
        assertArrayEquals(COMB_2_1, array(iterable(2, 1)));
        assertArrayEquals(COMB_2_2, array(iterable(2, 2)));
        assertArrayEquals(COMB_2_3, array(iterable(2, 3)));
        assertArrayEquals(COMB_3_0, array(iterable(3, 0)));
        assertArrayEquals(COMB_3_1, array(iterable(3, 1)));
        assertArrayEquals(COMB_3_2, array(iterable(3, 2)));
        assertArrayEquals(COMB_3_3, array(iterable(3, 3)));
        assertArrayEquals(COMB_4_0, array(iterable(4, 0)));
        assertArrayEquals(COMB_4_1, array(iterable(4, 1)));
        assertArrayEquals(COMB_4_2, array(iterable(4, 2)));
        assertArrayEquals(COMB_4_3, array(iterable(4, 3)));
        assertArrayEquals(COMB_4_4, array(iterable(4, 4)));
        assertArrayEquals(COMB_4_5, array(iterable(4, 5)));
    }

}
