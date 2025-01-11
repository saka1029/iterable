package saka1029.iterable;

import static org.junit.Assert.assertArrayEquals;
import static saka1029.iterable.Iterables.*;
import static saka1029.iterable.TestPermutation.*;
import java.util.Iterator;
import org.junit.Test;

public class TestSyncHolder {

    public static class SyncHolder<T> {

        T value = null;
        boolean filled = false;

        public synchronized void set(T newValue) {
            while (filled)
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            value = newValue;
            filled = true;
            notify();
        }

        public synchronized T get() {
            while (!filled)
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            T result = value;
            filled = false;
            notify();
            return result;
        }
    }

    public static Iterable<int[]> permutation(int n, int k) {
        return () -> new Iterator<>() {
            int[] selected = new int[k];
            boolean[] used = new boolean[n];
            SyncHolder<int[]> holder = new SyncHolder<>();
            int[] received = null;
            {
                Thread.ofVirtual().start(() -> {
                    solve(0);
                    holder.set(null);
                });
            }

            private void solve(int i) {
                if (i >= k)
                    holder.set(selected.clone());
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
                return (received = holder.get()) != null;
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
}
