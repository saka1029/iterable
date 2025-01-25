package saka1029.iterable;

import static org.junit.Assert.assertArrayEquals;
import static saka1029.iterable.Iterables.*;
import static saka1029.iterable.TestPermutation.*;
import java.util.Arrays;
import java.util.Iterator;
import org.junit.Test;

public class TestPermutationNext {

    static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    static void reverse(int[] array, int start, int end) {
        while (start < end)
            swap(array, start++, end--);
    }

    static boolean next(int[] array, int k) {
        int length = array.length;
        int i = length - 2;
        while (i >= 0 && array[i] >= array[i + 1])
            --i;
        if (i < 0)
            return false;
        int j = length - 1;
        while (array[i] >= array[j])
            --j;
        swap(array, i, j);
        reverse(array, i + 1, length - 1);
        return true;
    }

    @Test
    public void testNext() {
        int[] perm = intArray(range(0, 4, 1));
        do {
            System.out.println(Arrays.toString(perm));
        } while (next(perm, 3));
    }

    public static Iterable<int[]> iterable(int n, int k) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        if (k < 0) throw new IllegalArgumentException("k must be >= 0");
        return () -> new Iterator<>() {
            int[] selected = new int[k];            // 結果を格納する配列。
            { if (k > 0) selected[0] = -1; }        // 先頭の値を初期値にする。
            boolean[] used = new boolean[n];        // 値が使用済みであることを判別する配列。
            int i = 0;                              // 格納位置。
            boolean hasNext = advance();            // 結果があるかどうかを保持する変数。

            private boolean advance() {             // 次の結果があるかどうかを返す。
                while (true) {
                    if (i < 0)                      // 配置位置が先頭以前に戻ったとき
                        return false;               // すべての組み合わせが終了した。
                    if (i >= k) {                   // 配置が完了した。
                        --i;                        // 次の配置位置は最後
                        return true;                // 結果を返す。
                    }
                    int j = selected[i];            // 現在配置されている値。
                    if (j >= 0)                     // 現在配置されている値が初期値でなければ
                        used[j] = false;            // 未使用とする。
                    while (++j < n && used[j])      // 未使用の値を見つける。
                        /* do nothing */;
                    if (j >= n)                     // 次に配置する値が上限を超えていたら
                        --i;                        // 配置位置を前に戻す。
                    else {                          // 配置が有効なら
                        used[selected[i] = j] = true;  // 配置して値を使用済にする。
                        if (++i < k)                // 配置位置を次に進める。
                            selected[i] = -1;
                    }
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
        assertArrayEquals(PERM_0_0, int2dArray(iterable(0, 0)));
        assertArrayEquals(PERM_0_1, int2dArray(iterable(0, 1)));
        assertArrayEquals(PERM_0_2, int2dArray(iterable(0, 2)));
        assertArrayEquals(PERM_0_3, int2dArray(iterable(0, 3)));
        assertArrayEquals(PERM_1_0, int2dArray(iterable(1, 0)));
        assertArrayEquals(PERM_1_1, int2dArray(iterable(1, 1)));
        assertArrayEquals(PERM_1_2, int2dArray(iterable(1, 2)));
        assertArrayEquals(PERM_1_3, int2dArray(iterable(1, 3)));
        assertArrayEquals(PERM_2_0, int2dArray(iterable(2, 0)));
        assertArrayEquals(PERM_2_1, int2dArray(iterable(2, 1)));
        assertArrayEquals(PERM_2_2, int2dArray(iterable(2, 2)));
        assertArrayEquals(PERM_2_3, int2dArray(iterable(2, 3)));
        assertArrayEquals(PERM_3_0, int2dArray(iterable(3, 0)));
        assertArrayEquals(PERM_3_1, int2dArray(iterable(3, 1)));
        assertArrayEquals(PERM_3_2, int2dArray(iterable(3, 2)));
        assertArrayEquals(PERM_3_3, int2dArray(iterable(3, 3)));
        assertArrayEquals(PERM_4_0, int2dArray(iterable(4, 0)));
        assertArrayEquals(PERM_4_1, int2dArray(iterable(4, 1)));
        assertArrayEquals(PERM_4_2, int2dArray(iterable(4, 2)));
        assertArrayEquals(PERM_4_3, int2dArray(iterable(4, 3)));
        assertArrayEquals(PERM_4_4, int2dArray(iterable(4, 4)));
    }

}
