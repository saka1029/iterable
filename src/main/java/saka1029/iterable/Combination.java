package saka1029.iterable;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

public class Combination {

    private Combination() {
    }

    public static int count(int n, int k) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        if (k < 0) throw new IllegalArgumentException("r must be >= 0");
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

            final int[] selection = IntStream.range(0, k).toArray();
            boolean hasNext;

            private boolean advance() {
                for (int i = k - 1; i >= 0; )
                    if (++selection[i] >= n)
                        --i;
                    else if (i + 1 >= k)
                        return true;
                    else
                        selection[i + 1] = selection[i++];
                return false;
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public int[] next() {
                int[] result = selection.clone();
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
