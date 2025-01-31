package saka1029.iterable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Iterables {

    private Iterables() {
    }

    /*
     * Functions
     */

    public static <T> Function<T, T> identity() {
        return Function.identity();
    }

    /*
     * Generators
     */

    @SafeVarargs
    public static <T> List<T> listOf(T... elements) {
        return List.of(elements);
    }

    @SafeVarargs
    public static <T> List<T> listOf(Supplier<List<T>> constructor, T... elements) {
        List<T> result = constructor.get();
        for (T e : elements)
            result.add(e);
        return result;
    }

    public static List<Integer> intListOf(int... elements) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i : elements)
            result.add(i);
        return result;
    }

    public static int[] intArrayOf(int... elements) {
        return elements.clone();
    }

    @SafeVarargs
    public static <T> T[] arrayOf(T... elements) {
        return elements.clone();
    }

    public static Iterable<Integer> range(int start, int end, int step) {
        if (step == 0)
            throw new IllegalArgumentException("Invalid start, end, step");
        return () -> new Iterator<>() {
            int i = start;

            @Override
            public boolean hasNext() {
                return step > 0 ? i < end : i > end;
            }

            @Override
            public Integer next() {
                int result = i;
                i += step;
                return result;
            }
        };
    }

    public static Iterable<Integer> range(int start, int end) {
        return range(start, end, start <= end ? 1 : -1);
    }

    public static <T> Iterable<T> iterable(Supplier<Stream<T>> source) {
        return () -> source.get().iterator();
    }

    /**
     * 要素を生成する手続き(GeneratorBody)からIterableを生成します。
     * 使用後にGeneratorをクローズする必要があります。
     * 以下はフィボナッチ数を無限に生成するGeneratorの使用例です。
     * <pre><code>
     *    try (Generator<Integer> fibonacci = generate(g -> {
     *        int a = 0, b = 1;
     *        while (true) {
     *            g.yield(a);
     *            int c = a + b;
     *            a = b;
     *            b = c;
     *        }
     *    })) {
     *        assertEquals(listOf(0, 1, 1, 2, 3, 5, 8, 13), list(limit(8, fibonacci)));
     *    }
     * </code></pre>
     * @param <T> 生成する要素の型です。
     * @param body 要素を生成する手続きを指定します。
     * @return
     */
    public static <T> Generator<T> generate(Generator.Body<T> body) {
        return new Generator<>(body);
    }

    /**
     * n個の要素からk個取り出す順列の数を返します。
     * @param n 全体の要素の数を指定します。
     * @param k 取り出す要素の数を指定します。
     * @return 順列の数を返します。
     */
    public static int permutationCount(int n, int k) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        if (k < 0) throw new IllegalArgumentException("k must be >= 0");
        int permutation = 1;
        for (; k > 0; --k, --n)
            permutation *= n;
        return permutation;
    }

    /**
     * n個の数字(0からn-1)からk個取り出す順列を返します。
     * @param n 要素の数を指定します。
     * @param k 取り出す要素の数を指定します。
     * @return 順列(int[k])のIterableを返します。
     */
    public static Iterable<int[]> permutation(int n, int k) {
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

    public static <T> Iterable<List<T>> permutation(List<T> list, int k) {
        return indexMap(list, permutation(list.size(), k));
    }

    /**
     * n個の要素からk個取り出す組み合わせの数を返します。
     * @param n 全体の要素の数を指定します。
     * @param k 取り出す要素の数を指定します。
     * @return 組み合わせの数を返します。
     */
    public static int combinationCount(int n, int k) {
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

    public static Iterable<int[]> combination(int n, int k) {
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

    public static <T> Iterable<List<T>> combination(List<T> list, int k) {
        return indexMap(list, combination(list.size(), k));
    }

    /*
     * Translators
     */
    public static <T, U> Iterable<U> map(Function<T, U> mapper, Iterable<T> source) {
        return () -> new Iterator<>() {
            Iterator<T> iterator = source.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public U next() {
                return mapper.apply(iterator.next());
            }
        };
    }

    public static <T, U> Iterable<U> flatMap(
            Function<? super T,? extends Iterable<? extends U>> mapper, Iterable<T> source) {
        return () -> new Iterator<>() {
            final Iterator<T> parent = source.iterator();
            Iterator<? extends U> child = null;
            boolean hasNext = advance();
            U next;

            private boolean advance() {
                while (true) {
                    if (child == null) {
                        if (!parent.hasNext())
                            return false;
                        child = mapper.apply(parent.next()).iterator();
                    }
                    if (child.hasNext()) {
                        next = child.next();
                        return true;
                    }
                    child = null;
                }
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public U next() {
                U result = next;
                hasNext = advance();
                return result;
            }
        };
    }

    public static <T, U, V> Iterable<V> zip(BiFunction<T, U, V> zipper, Iterable<T> left, Iterable<U> right) {
        return () -> new Iterator<>() {
            Iterator<T> l = left.iterator();
            Iterator<U> r = right.iterator();

            @Override
            public boolean hasNext() {
                return l.hasNext() && r.hasNext();
            }

            @Override
            public V next() {
                return zipper.apply(l.next(), r.next());
            }
        };
    }

    public static Iterable<int[]> indexMap(int[] list, Iterable<int[]> source) {
        return map(ints -> {
            int[] result = new int[ints.length];
            int j = 0;
            for (int i : ints)
                result[j++] = list[i];
            return result;
        }, source);
    }

    public static <T> Iterable<List<T>> indexMap(List<T> list, Iterable<int[]> source) {
        return map(ints -> {
            List<T> result = new ArrayList<>();
            for (int i : ints)
                result.add(list.get(i));
            return result;
        }, source);
    }

    public static <T> Iterable<T> filter(Predicate<T> predicate, Iterable<T> source) {
        return () -> new Iterator<>() {

            final Iterator<T> iterator = source.iterator();
            T next;
            boolean hasNext = advance();

            private boolean advance() {
                while (iterator.hasNext())
                    if (predicate.test(next = iterator.next()))
                        return true;
                return false;
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public T next() {
                T result = next;
                hasNext = advance();
                return result;
            }
        };
    }

    public static <T> Iterable<T> limit(int max, Iterable<T> source) {
        return () -> new Iterator<>() {

            Iterator<T> iterator = source.iterator();
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < max && iterator.hasNext();
            }
            @Override
            public T next() {
                ++i;
                return iterator.next();
            }
        };
    }

    public static <T> Iterable<T> dropWhile(Predicate<T> predicate, Iterable<T> source) {
        return () -> new Iterator<>() {
            Iterator<T> iterator = source.iterator();
            {
                while (iterator.hasNext() && predicate.test(iterator.next()))
                    /* do nothing */;
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }
        };
    }

    public static <T> Iterable<T> takeWhile(Predicate<T> predicate, Iterable<T> source) {
        return () -> new Iterator<>() {
            Iterator<T> iterator = source.iterator();
            boolean hasNext = advance();
            T next;

            private boolean advance() {
                return iterator.hasNext() && predicate.test(next = iterator.next());
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public T next() {
                T result = next;
                hasNext = advance();
                return result;
            }
        };
    }

    public static <T> Iterable<T> distinct(Iterable<T> source) {
        Set<T> set = new LinkedHashSet<>();
        for (T e : source)
            set.add(e);
        return set;
    }

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
    
    /*
     * Terminator
     */
    public static <T> boolean allMatch(Predicate<? super T> predicate, Iterable<T> source) {
        for (T e : source)
            if (!predicate.test(e))
                return false;
        return true;
    }

    public static <T> boolean anyMatch(Predicate<? super T> predicate, Iterable<T> source) {
        for (T e : source)
            if (predicate.test(e))
                return true;
        return false;
    }

    public static <T> int count(Iterable<T> source) {
        int count = 0;
        for (@SuppressWarnings("unused") T e : source)
            ++count;
        return count;
    }

    public static <T> T[] array(IntFunction<T[]> constructor, Iterable<T> source) {
        return stream(source).toArray(constructor);
    }

    public static int[][] int2dArray(Iterable<int[]> source) {
        return stream(source).toArray(int[][]::new);
    }

    public static int[] intArray(Iterable<Integer> source) {
        return stream(source).mapToInt(Integer::valueOf).toArray();
    }

    public static <T> List<T> list(Supplier<List<T>> constructor, Iterable<T> source) {
        List<T> result = constructor.get();
        for (T e : source)
            result.add(e);
        return result;
    }

    public static <T> List<T> list(Iterable<T> source) {
        return list(ArrayList::new, source);
    }

    public static <T, U, V> Map<U, V> map(Supplier<Map<U, V>> constructor,
            Function<T, U> keyExtructor, Function<T, V> valueExtractor, Iterable<T> source) {
        Map<U, V> result = constructor.get();
        for (T e : source)
            result.put(keyExtructor.apply(e), valueExtractor.apply(e));
        return result;
    }
    public static <T, U, V> Map<U, V> map(Function<T, U> keyExtructor,
            Function<T, V> valueExtractor, Iterable<T> source) {
        return map(HashMap::new, keyExtructor, valueExtractor, source);
    }

    public static <U, V> Map<U, V> map(Supplier<Map<U, V>> constructor,
            Iterable<U> keySource, Iterable<V> valueSource) {
        Map<U, V> result = constructor.get();
        Iterator<U> key = keySource.iterator();
        Iterator<V> value = valueSource.iterator();
        while (key.hasNext() && value.hasNext())
            result.put(key.next(), value.next());
        return result;
    }

    public static <U, V> Map<U, V> map(Iterable<U> keySource, Iterable<V> valueSource) {
        return map(HashMap::new, keySource, valueSource);
    }

    public static <T, U> U reduce(U unit, BiFunction<U, T, U> reducer, Iterable<T> source) {
        U result = unit;
        for (T e : source)
            result = reducer.apply(result, e);
        return result;
    }

    public static <T> void forEach(Consumer<? super T> consumer, Iterable<T> source) {
        for (T e : source)
            consumer.accept(e);
    }

    public static <T> T findAny(Predicate<? super T> predicate, Iterable<T> source) {
        for (T e : source)
            if (predicate.test(e))
                return e;
        return null;
    }

    public static <T> T findFirst(Iterable<T> source) {
        Iterator<T> iterator = source.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }
}
