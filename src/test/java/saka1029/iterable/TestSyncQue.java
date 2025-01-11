package saka1029.iterable;

import static org.junit.Assert.assertArrayEquals;
import static saka1029.iterable.Iterables.*;
import static saka1029.iterable.TestPermutation.*;
import static saka1029.iterable.TestCombination.*;
import java.util.Iterator;
import org.junit.Test;

public class TestSyncQue {

    public static Iterable<int[]> permutation(int n, int k) {
        return () -> new Iterator<>() {
            int[] selected = new int[k];
            boolean[] used = new boolean[n];
            SyncQue<int[]> que = new SyncQue<>(5);
            int[] received = null;
            {
                SyncQue.start(() -> {
                    solve(0);
                    que.add(null);
                });
            }

            private void solve(int i) {
                if (i >= k)
                    que.add(selected.clone());
                else
                    for (int j = 0; j < n; ++j)
                        if (!used[j]) {
                            used[j] = true;
                            selected[i] = j;
                            solve(i + 1);
                            used[j] = false;
                        }
            }

            boolean hasNext = advance();

            private boolean advance() {
                return (received = que.remove()) != null;
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public int[] next() {
                int[] result = received;
                hasNext = advance();
                return result;
            }
        };

    }

    @Test
    public void testIterable() {
        assertArrayEquals(PERM_0_0, array(permutation(0, 0)));
        assertArrayEquals(PERM_0_1, array(permutation(0, 1)));
        assertArrayEquals(PERM_0_2, array(permutation(0, 2)));
        assertArrayEquals(PERM_0_3, array(permutation(0, 3)));
        assertArrayEquals(PERM_1_0, array(permutation(1, 0)));
        assertArrayEquals(PERM_1_1, array(permutation(1, 1)));
        assertArrayEquals(PERM_1_2, array(permutation(1, 2)));
        assertArrayEquals(PERM_1_3, array(permutation(1, 3)));
        assertArrayEquals(PERM_2_0, array(permutation(2, 0)));
        assertArrayEquals(PERM_2_1, array(permutation(2, 1)));
        assertArrayEquals(PERM_2_2, array(permutation(2, 2)));
        assertArrayEquals(PERM_2_3, array(permutation(2, 3)));
        assertArrayEquals(PERM_3_0, array(permutation(3, 0)));
        assertArrayEquals(PERM_3_1, array(permutation(3, 1)));
        assertArrayEquals(PERM_3_2, array(permutation(3, 2)));
        assertArrayEquals(PERM_3_3, array(permutation(3, 3)));
        assertArrayEquals(PERM_4_0, array(permutation(4, 0)));
        assertArrayEquals(PERM_4_1, array(permutation(4, 1)));
        assertArrayEquals(PERM_4_2, array(permutation(4, 2)));
        assertArrayEquals(PERM_4_3, array(permutation(4, 3)));
        assertArrayEquals(PERM_4_4, array(permutation(4, 4)));
    }

    static Iterable<int[]> combination(int n, int k) {
        return () -> new Iterator<>() {
            int[] selected = new int[k];
            SyncQue<int[]> syncQue = new SyncQue<>(5);
            {
                Thread.ofVirtual().start(() -> { solve(0, 0); syncQue.add(null); });
            }

            private void solve(int i, int j) {
                if (i >= k)
                    syncQue.add(selected.clone());
                else
                    for (; j < n; ++j) {
                        selected[i] = j;
                        solve(i + 1, j + 1);
                    }
            }

            int[] received;
            boolean hasNext = advance();

            private boolean advance() {
                return (received = syncQue.remove()) != null;
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public int[] next() {
                int[] result = received;
                hasNext = advance();
                return result;
            }
        };
    }

    @Test
    public void testCombination() {
        assertArrayEquals(COMB_0_0, array(combination(0, 0)));
        assertArrayEquals(COMB_0_1, array(combination(0, 1)));
        assertArrayEquals(COMB_0_2, array(combination(0, 2)));
        assertArrayEquals(COMB_0_3, array(combination(0, 3)));
        assertArrayEquals(COMB_1_0, array(combination(1, 0)));
        assertArrayEquals(COMB_1_1, array(combination(1, 1)));
        assertArrayEquals(COMB_1_2, array(combination(1, 2)));
        assertArrayEquals(COMB_1_3, array(combination(1, 3)));
        assertArrayEquals(COMB_2_0, array(combination(2, 0)));
        assertArrayEquals(COMB_2_1, array(combination(2, 1)));
        assertArrayEquals(COMB_2_2, array(combination(2, 2)));
        assertArrayEquals(COMB_2_3, array(combination(2, 3)));
        assertArrayEquals(COMB_3_0, array(combination(3, 0)));
        assertArrayEquals(COMB_3_1, array(combination(3, 1)));
        assertArrayEquals(COMB_3_2, array(combination(3, 2)));
        assertArrayEquals(COMB_3_3, array(combination(3, 3)));
        assertArrayEquals(COMB_4_0, array(combination(4, 0)));
        assertArrayEquals(COMB_4_1, array(combination(4, 1)));
        assertArrayEquals(COMB_4_2, array(combination(4, 2)));
        assertArrayEquals(COMB_4_3, array(combination(4, 3)));
        assertArrayEquals(COMB_4_4, array(combination(4, 4)));
        assertArrayEquals(COMB_4_5, array(combination(4, 5)));
    }
}
