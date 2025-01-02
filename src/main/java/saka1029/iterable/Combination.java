package saka1029.iterable;

import java.util.Iterator;
import java.util.List;

public class Combination {

    private Combination() {
    }

    /**
     * n個からk個取り出すときの組み合わせ数を返します。
     * nCr = n! / (n - k)!
     * 【k &gt; n のとき 0 とする理由】
     * k &gt; n のときは分母が負の階乗となって、
     * この式では計算できません。
     * ただしWikipediaには以下の記述があります。
     * 二項展開の係数として数 nCk を定義するものと考えれば
     * k = n または k = 0 のとき nCk = 1 ,
     * k &gt; n のとき nCk = 0
     * と考えるのは自然である。
     * (https://ja.wikipedia.org/wiki/%E7%B5%84%E5%90%88%E3%81%9B_(%E6%95%B0%E5%AD%A6)#%E5%AE%9A%E7%BE%A9)
     * @param n
     * @param k
     * @return
     */
    public static int count(int n, int k) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        if (k < 0) throw new IllegalArgumentException("r must be >= 0");
        if (k > n)
            return 0;
        k = Math.min(k, n - k);
        int count = 1;
        for(int i = 1, j = n - i + 1; i <= k; ++i, --j)
            count = count * j / i;
        return count;
    }

    public static Iterable<int[]> iterable(int n, int k) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        if (k < 0) throw new IllegalArgumentException("k must be >= 0");
        return () -> new Iterator<>() {
            final int nmk = n - k;                          // 各位置における最大値(n - k + i)を求める定数。
            final int[] selected = new int[k];              // 結果を格納する配列。
            {
                for (int i = 0; i < k; ++i)                 // {0, 1, 2, ... , k - 1}で初期化
                    selected[i] = i;
            }

            boolean hasNext = k <= n;                       // k > n のときは該当なし。

            private boolean advance() {
                int i = k - 1;                              // 最後の格納位置。
                while (i >= 0 && selected[i] >= nmk + i)    // 末尾から最大値を超えない位置を探す。
                    --i;
                if (i < 0)                                  // 見つからない場合は終了する。
                    return false;
                int next = ++selected[i];                   // 次の値に進める。
                while (++i < k)                             // それ以降の位置は残りの最小値を設定する。
                    selected[i] = ++next;
                return true;
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
