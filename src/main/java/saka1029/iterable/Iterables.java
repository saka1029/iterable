package saka1029.iterable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
     * Constructors
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

    // public static <T, U> Iterable<U> generate(Supplier<T> seed, Predicate<T> hasNext, Function<T, U> next) {
    //     return () -> new Iterator<>() {
    //         T t = seed.get();

    //         @Override
    //         public boolean hasNext() {
    //             return hasNext.test(t);
    //         }

    //         @Override
    //         public U next() {
    //             return next.apply(t);
    //         }
    //     };
    // }

    public static <T> Iterable<T> iterable(Supplier<Stream<T>> source) {
        return () -> source.get().iterator();
    }

    /**
     * StreamをIterableに変換します。
     * ただし変換後のIterableに対して、
     * iterator()は一度しか呼び出せない点に注意する必要があります。
     * @param <T>
     * @param source 変換するStreamを指定します。
     * @return StreamをIterable<T>に変換した結果を返します。
     */
    public static <T> Iterable<T> iterable(Stream<T> source) {
        return source::iterator;
    }

    /*
     * Converters
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

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
    
    /*
     * Terminator
     */
    public static <T> T[] array(IntFunction<T[]> constructor, Iterable<T> source) {
        return stream(source).toArray(constructor);
    }

    public static int[][] array(Iterable<int[]> source) {
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
