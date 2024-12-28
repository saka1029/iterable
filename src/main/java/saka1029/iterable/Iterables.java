package saka1029.iterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Iterables {

    private Iterables() {
    }

    @SafeVarargs
    public static <T> List<T> listOf(T... elements) {
        return List.of(elements);
    }

    @SafeVarargs
    public static <T> List<T> listOf(Supplier<List<T>> creator, T... elements) {
        List<T> result = creator.get();
        for (T e : elements)
            result.add(e);
        return result;
    }

    public static <T> List<T> list(Supplier<List<T>> creator, Iterable<T> source) {
        List<T> result = creator.get();
        for (T e : source)
            result.add(e);
        return result;
    }

    public static <T> List<T> list(Iterable<T> source) {
        return arrayList(source);
    }

    public static List<Integer> intListOf(int... elements) {
        List<Integer> result = new ArrayList<>();
        for (int i : elements)
            result.add(i);
        return result;
    }

    @SafeVarargs
    public static <T> ArrayList<T> arrayListOf(T... elements) {
        return (ArrayList<T>) listOf(ArrayList::new, elements);
    }

    public static <T> ArrayList<T> arrayList(Iterable<T> source) {
    return (ArrayList<T>) list(ArrayList::new, source);
    }

    @SafeVarargs
    public static <T> LinkedList<T> linkedListOf(T... elements) {
        return (LinkedList<T>) listOf(LinkedList::new, elements);
    }

    public static <T> LinkedList<T> linkedList(Iterable<T> source) {
        return (LinkedList<T>) list(LinkedList::new, source);
    }

    public static int[] arrayOf(int... elements) {
        return elements.clone();
    }

    public static <T> T[] array(IntFunction<T[]> creator, Iterable<T> source) {
        return stream(source).toArray(creator);
    }

    public static int[][] array(Iterable<int[]> source) {
        return stream(source).toArray(int[][]::new);
    }

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

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
