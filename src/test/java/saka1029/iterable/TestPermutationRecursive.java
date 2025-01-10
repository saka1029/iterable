package saka1029.iterable;

import static org.junit.Assert.assertArrayEquals;
import static saka1029.iterable.TestPermutation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Test;

public class TestPermutationRecursive {

    static void permutation(int n, int k, Consumer<int[]> callback) {
        new Object() {
            int[] result = new int[k];
            boolean[] used = new boolean[n];

            void set(int i, int j) {
                used[j] = true;
                result[i] = j;
                solve(i + 1);
                used[j] = false;
            }

            void solve(int i) {
                if (i >= k)
                    callback.accept(result.clone());
                else
                    for (int j = 0; j < n; ++j)
                        if (!used[j])
                            set(i, j);
            }
        }.solve(0);
    }

    static int[][] permutation(int n, int k) {
        List<int[]> a = new ArrayList<>();
        permutation(n, k, ints -> a.add(ints));
        int[][] r = a.stream().toArray(int[][]::new);
        return r;
    }

    @Test
    public void testPermutation() {
        assertArrayEquals(PERM_0_0, permutation(0, 0));
        assertArrayEquals(PERM_0_1, permutation(0, 1));
        assertArrayEquals(PERM_0_2, permutation(0, 2));
        assertArrayEquals(PERM_0_3, permutation(0, 3));
        assertArrayEquals(PERM_1_0, permutation(1, 0));
        assertArrayEquals(PERM_1_1, permutation(1, 1));
        assertArrayEquals(PERM_1_2, permutation(1, 2));
        assertArrayEquals(PERM_1_3, permutation(1, 3));
        assertArrayEquals(PERM_2_0, permutation(2, 0));
        assertArrayEquals(PERM_2_1, permutation(2, 1));
        assertArrayEquals(PERM_2_2, permutation(2, 2));
        assertArrayEquals(PERM_2_3, permutation(2, 3));
        assertArrayEquals(PERM_3_0, permutation(3, 0));
        assertArrayEquals(PERM_3_1, permutation(3, 1));
        assertArrayEquals(PERM_3_2, permutation(3, 2));
        assertArrayEquals(PERM_3_3, permutation(3, 3));
        assertArrayEquals(PERM_4_0, permutation(4, 0));
        assertArrayEquals(PERM_4_1, permutation(4, 1));
        assertArrayEquals(PERM_4_2, permutation(4, 2));
        assertArrayEquals(PERM_4_3, permutation(4, 3));
        assertArrayEquals(PERM_4_4, permutation(4, 4));
    }

}
