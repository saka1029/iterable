package saka1029.iterable;

import static saka1029.iterable.TestPermutation.*;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static saka1029.iterable.Iterables.*;
import org.junit.Test;

public class TestGenerator {

    @Test
    public void testGenerator() {
        try (Generator<Integer> g = Generator.of(q -> {
            q.yield(1);
            q.yield(0);
            q.yield(3);
        })) {
            assertEquals(listOf(1, 0, 3), list(g));
        }
    }

    @Test
    public void testGeneratorStream() {
        try (Generator<Integer> g = Generator.of(q -> {
            q.yield(1);
            q.yield(0);
            q.yield(3);
        })) {
            assertEquals(List.of(1, 0, 3), g.stream().toList());
        }
    }

    @Test
    public void testGeneratorIterator() {
        try (Generator<Integer> g = Generator.of(q -> {
            q.yield(1);
            q.yield(0);
            q.yield(3);
        })) {
            Iterator<Integer> iterator = g.iterator();
            assertTrue(iterator.hasNext()); assertEquals(1, (int)iterator.next());
            assertTrue(iterator.hasNext()); assertEquals(0, (int)iterator.next());
            assertTrue(iterator.hasNext()); assertEquals(3, (int)iterator.next());
            assertFalse(iterator.hasNext());
        }
    }

    @Test
    public void testGeneratorContextTake() {
        try (Generator<Integer> g = Generator.of(q -> {
            q.yield(1);
            q.yield(0);
            q.yield(3);
        })) {
            Generator.Context<Integer> context = g.context();
            assertEquals(1, (int)context.take());
            assertEquals(0, (int)context.take());
            assertEquals(3, (int)context.take());
            assertNull(context.take());
        }
    }

    @Test
    public void testGeneratorContextTakeTooMuch() {
        try (Generator<Integer> g = Generator.of(q -> {
            q.yield(1);
            q.yield(0);
            q.yield(3);
        })) {
            Generator.Context<Integer> context = g.context();
            assertEquals(1, (int)context.take());
            assertEquals(0, (int)context.take());
            assertEquals(3, (int)context.take());
            assertNull(context.take());
            try {
                context.take();
                fail();
            } catch (NoSuchElementException e) {
                assertEquals("No yield element", e.getMessage());
            }
        }
    }

    @Test
    public void testFibonacci() {
        try (Generator<Integer> fibonacci = Generator.of(context -> {
            int a = 0, b = 1;
            while (true) {
                context.yield(a);
                int c = a + b;
                a = b;
                b = c;
            }
        })) {
            assertEquals(listOf(0, 1, 1, 2, 3, 5, 8, 13), list(limit(8, fibonacci)));
        }
    }

    static void fibonacci(Generator.Context<Integer> context) throws InterruptedException {
        int a = 0, b = 1;
        while (true) {
            context.yield(a);
            int temp = a + b;
            a = b;
            b = temp;
        }
    }

    public void testFibonacciStatic() {
        Generator<Integer> fibonacci = Generator.of(TestGenerator::fibonacci);
        assertEquals(listOf(0, 1, 1, 2, 3, 5, 8, 13), list(limit(8, fibonacci)));
        assertEquals(listOf(0, 1, 1, 2, 3, 5, 8, 13), list(limit(8, Generator.of(TestGenerator::fibonacci))));
    }

    static Generator<int[]> permutation(int n, int k) {
        return generate(context -> {
            new Object() {
                int[] selected = new int[k];
                boolean[] used = new boolean[n];

                void solve(int i) throws InterruptedException {
                    if (i >= k)
                        context.yield(selected.clone());
                    else
                        for (int j = 0; j < n; ++j) {
                            if (!used[j]) {
                                used[j] = true;
                                selected[i] = j;
                                solve(i + 1);
                                used[j] = false;
                            }
                        }
                }
            }.solve(0);
        });
    }

    @Test
    public void testPermutaion() {
        assertArrayEquals(PERM_0_0, iny2dArray(permutation(0, 0)));
        assertArrayEquals(PERM_0_1, iny2dArray(permutation(0, 1)));
        assertArrayEquals(PERM_0_2, iny2dArray(permutation(0, 2)));
        assertArrayEquals(PERM_0_3, iny2dArray(permutation(0, 3)));
        assertArrayEquals(PERM_1_0, iny2dArray(permutation(1, 0)));
        assertArrayEquals(PERM_1_1, iny2dArray(permutation(1, 1)));
        assertArrayEquals(PERM_1_2, iny2dArray(permutation(1, 2)));
        assertArrayEquals(PERM_1_3, iny2dArray(permutation(1, 3)));
        assertArrayEquals(PERM_2_0, iny2dArray(permutation(2, 0)));
        assertArrayEquals(PERM_2_1, iny2dArray(permutation(2, 1)));
        assertArrayEquals(PERM_2_2, iny2dArray(permutation(2, 2)));
        assertArrayEquals(PERM_2_3, iny2dArray(permutation(2, 3)));
        assertArrayEquals(PERM_3_0, iny2dArray(permutation(3, 0)));
        assertArrayEquals(PERM_3_1, iny2dArray(permutation(3, 1)));
        assertArrayEquals(PERM_3_2, iny2dArray(permutation(3, 2)));
        assertArrayEquals(PERM_3_3, iny2dArray(permutation(3, 3)));
        assertArrayEquals(PERM_4_0, iny2dArray(permutation(4, 0)));
        assertArrayEquals(PERM_4_1, iny2dArray(permutation(4, 1)));
        assertArrayEquals(PERM_4_2, iny2dArray(permutation(4, 2)));
        assertArrayEquals(PERM_4_3, iny2dArray(permutation(4, 3)));
        assertArrayEquals(PERM_4_4, iny2dArray(permutation(4, 4)));
    }
}
