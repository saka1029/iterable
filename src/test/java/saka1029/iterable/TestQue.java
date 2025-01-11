package saka1029.iterable;

import static org.junit.Assert.assertArrayEquals;
import static saka1029.iterable.Iterables.*;
import static saka1029.iterable.TestPermutation.*;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Test;

public class TestQue {

    public static class Que<T> {

        final int capacity;
        final Deque<T> que = new LinkedList<>();

        public Que(int capacity) {
            this.capacity = capacity;
        }

        public synchronized void add(T newValue) {
            try {
                while (que.size() >= capacity)
                    wait();
                que.add(newValue);
                notify();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public synchronized T remove() {
            try {
                while (que.size() <= 0)
                    wait();
                T result = que.remove();
                notify();
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public static Thread start(Runnable runnable) {
            return Thread.ofVirtual().start(runnable);
        }
    }

    public static Iterable<int[]> permutation(int n, int k) {
        return () -> new Iterator<>() {
            int[] selected = new int[k];
            boolean[] used = new boolean[n];
            Que<int[]> que = new Que<>(5);
            int[] received = null;
            {
                Que.start(() -> {
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
}
