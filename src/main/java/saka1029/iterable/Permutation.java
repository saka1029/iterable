package saka1029.iterable;

import java.util.Iterator;
import java.util.List;

public class Permutation {

    private Permutation() {
    }

    public static int count(int n, int k) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        if (k < 0) throw new IllegalArgumentException("k must be >= 0");
        int permutation = 1;
        for (; k > 0; --k, --n)
            permutation *= n;
        return permutation;
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

    public static <T> Iterable<int[]> iterable(int[] choices, int k) {
        return Iterables.indexMap(choices, iterable(choices.length, k));
    }

    public static <T> Iterable<List<T>> iterable(List<T> choices, int k) {
        return Iterables.indexMap(choices, iterable(choices.size(), k));
    }
}
