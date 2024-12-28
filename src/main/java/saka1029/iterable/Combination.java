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

            int i = 0;  // 次に格納する場所
            int j = 0;  // 次に格納する値
            final int[] selected = new int[k];
            boolean hasNext = advance();

            private boolean advance() {
                // if (k > n)
                //     return false;
                while (true) {
                    if (i >= k) {                   // すべての値が格納されたら
                        if (--i >= 0)
                            j = selected[i] + 1;    // 次の格納位置は(k -1)、格納する値は現在格納されている値 + 1
                        return true;                // 結果を返す。
                    }
                    if (i < 0 || j >= n && i <= 0) { // 先頭で格納する値がなければすべて終了する。
                        System.out.printf("n=%d k=%d i=%d j=%d%n", n, k, i, j);
                        return false;
                    }
                    if (j >= n)                     // 格納する値が最大値を超えたら
                        j = selected[--i] + 1;      // 一つ前に戻る。
                    else
                        selected[i++] = j++;        // 格納して次の位置へ進む。
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
