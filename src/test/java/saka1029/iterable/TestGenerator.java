package saka1029.iterable;

import static saka1029.iterable.TestPermutation.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static saka1029.iterable.Iterables.*;
import org.junit.Test;

public class TestGenerator {

    @Test
    public void testGenerate() {
        try (Generator<Integer> g = generate(q -> {
            q.yield(1);
            q.yield(0);
            q.yield(3);
        })) {
            assertEquals(listOf(1, 0, 3), list(g));
        }
    }

    @Test
    public void testFibonacci() {
        try (Generator<Integer> fibonacci = generate(context -> {
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

    static void fibonacci(GeneratorContext<Integer> context) throws InterruptedException {
        int a = 0, b = 1;
        while (true) {
            context.yield(a);
            int temp = a + b;
            a = b;
            b = temp;
        }
    }

    public void testFibonacciStatic() {
        Generator<Integer> fibonacci = generate(TestGenerator::fibonacci);
        assertEquals(listOf(0, 1, 1, 2, 3, 5, 8, 13), list(limit(8, fibonacci)));
        assertEquals(listOf(0, 1, 1, 2, 3, 5, 8, 13), list(limit(8, generate(TestGenerator::fibonacci))));
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
