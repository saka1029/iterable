package saka1029.iterable;

import static org.junit.Assert.assertArrayEquals;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Test;

public class TestCombinationRecursive {

    /*
                                                                    int n, k
        new Object() {
                                                                    int i = 0
                                                                    int j = 0
                                                                    next = SOLVE
                                                                    callcount = 0
            int[] selected = new int[k];                            int[] selected = new int[k];
            void solve(int i, int j) {                      SOLVE:
                                                                    if callcount > 0 goto CALLED
                if (i >= k)                                         ++callcount
                                                                    if not i >= k goto ELSE
                    callback.accept(selected.clone());              callback.accept(selected.clone())
                                                                    return true;
                else                                        ELSE:
                    for (; j < n; ++j) {                    FOR:    if not j < n goto ENDIF
                        selected[i] = j;                            selected[i] = j
                                                                    push(i), push(j)
                        solve(i + 1, j + 1);                        ++i, ++j
                                                                    goto SOLVE
                                                            CALLED: j = pop(), i = pop()
                    }                                       FOREND: ++j
                                                                    goto FOR
            }                                               ENDIF:  if --callcount > 0 goto CALLED
                                                            END:    return false

        }.solve(0, 0);


     */
    public static void combination(int n, int k, Consumer<int[]> callback) {
        new Object() {
            int[] selected = new int[k];
            void solve(int i, int j) {
                if (i >= k)
                    callback.accept(selected.clone());
                else
                    for (; j < n; ++j) {
                        selected[i] = j;
                        solve(i + 1, j + 1);
                    }
            }
        }.solve(0, 0);
    }

    static int[][] combination(int n, int k) {
        List<int[]> a = new ArrayList<>();
        combination(n, k, ints -> a.add(ints));
        int[][] r = a.stream().toArray(int[][]::new);
        return r;
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
    public void testCombination() {
        assertArrayEquals(COMB_0_0, combination(0, 0));
        assertArrayEquals(COMB_0_1, combination(0, 1));
        assertArrayEquals(COMB_0_2, combination(0, 2));
        assertArrayEquals(COMB_0_3, combination(0, 3));
        assertArrayEquals(COMB_1_0, combination(1, 0));
        assertArrayEquals(COMB_1_1, combination(1, 1));
        assertArrayEquals(COMB_1_2, combination(1, 2));
        assertArrayEquals(COMB_1_3, combination(1, 3));
        assertArrayEquals(COMB_2_0, combination(2, 0));
        assertArrayEquals(COMB_2_1, combination(2, 1));
        assertArrayEquals(COMB_2_2, combination(2, 2));
        assertArrayEquals(COMB_2_3, combination(2, 3));
        assertArrayEquals(COMB_3_0, combination(3, 0));
        assertArrayEquals(COMB_3_1, combination(3, 1));
        assertArrayEquals(COMB_3_2, combination(3, 2));
        assertArrayEquals(COMB_3_3, combination(3, 3));
    }

}
