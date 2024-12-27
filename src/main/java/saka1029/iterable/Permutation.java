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
            int[] selected = new int[k];        // 選択された数
            boolean[] used = new boolean[n];    // 使用済みの数
            int i = 0;                          // 次に選択するselected上の位置
            int j = 0;                          // 次に試す数
            boolean hasNext = advance();

            private boolean advance() {
                while (true) {
                    if (i < 0)                      // すべての組み合わせを試し終わった。
                        return false;
                    if (i >= k) {                   // すべての数を格納した。
                        i = k - 1;                  // 次回やり直す位置
                        if (i >= 0)
                            j = selected[i] + 1;    // 次回やり直す数
                        return true;                // 結果を返す。
                    }                               // 格納途中
                    if (j > 0)                      // 次回試す数がゼロ以外なら
                        used[selected[i]] = false;  // 前回の数を未使用にする。
                    for (;j < n; ++j)               // 未使用の数を探す。
                        if (!used[j])               // 見つかったら、
                            break;                  // ループを抜ける
                    if (j < n) {                    // 未使用の数が見つかった。(次に進む)
                        selected[i] = j;            // 見つかった数を格納する。
                        used[j] = true;             // 使用済みにする。
                        j = 0;                      // 次の位置はゼロから探す。
                        ++i;                        // 次の位置へ
                    } else {                        // 未使用の数が見つからなかった。(前に戻る)
                        if (--i >= 0)               // 前に戻る。
                            j = selected[i] + 1;    // 次に試す数
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public int[] next() {
                int[] r = selected.clone();
                hasNext = advance();
                return r;
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
