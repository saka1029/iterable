package saka1029.iterable;

import static org.junit.Assert.assertArrayEquals;
import static saka1029.iterable.Iterables.*;
import static saka1029.iterable.TestPermutation.*;
import java.util.Iterator;
import org.junit.Test;

public class TestSync {


    public static class Sync {

        public enum Status { EMPTY, SET, CLOSED }

        Status status = Status.EMPTY;

        public synchronized void set() throws InterruptedException {
            while (status == Status.SET)
                wait();
            if (status == Status.CLOSED)
                throw new IllegalStateException("closed");
            status = Status.SET;
            notify();
        }

        public synchronized void close() throws InterruptedException {
            while (status == Status.SET)
                wait();
            if (status == Status.CLOSED)
                throw new IllegalStateException("closed");
            status = Status.CLOSED;
            notify();
        }

        public synchronized Status get() throws InterruptedException {
            while (status == Status.EMPTY)
                wait();
            if (status == Status.CLOSED)
                throw new IllegalStateException("closed");
            Status result = status;
            notify();
            return result;
        }
    }

    public static Iterable<int[]> permutation(int n, int k) {
        return () -> new Iterator<>() {
            int[] selected = new int[k];
            boolean[] used = new boolean[n];
            Sync sync = new Sync();
            {
                Thread.ofVirtual().start(() -> {
                    new Object() {
                        void found() {
                            try {
                                sync.set();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        void set(int i, int j) {
                            used[j] = true;
                            selected[i] = j;
                            solve(i + 1);
                            used[j] = false;
                        }

                        void solve(int i) {
                            if (i >= k)
                                found();
                            else
                                for (int j = 0; j < n; ++j)
                                    if (!used[j])
                                        set(i, j);
                        }
                    }.solve(0);
                });
            }

            boolean hasNext = advance();

            private boolean advance() {
                try {
                    Sync.Status status = sync.get();
                    return status != Sync.Status.CLOSED;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
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
