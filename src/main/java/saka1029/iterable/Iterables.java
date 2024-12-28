package saka1029.iterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Iterables {

    private Iterables() {
    }

    @SafeVarargs
    public static <T> List<T> list(T... elements) {
        return List.of(elements);
    }

    @SafeVarargs
    public static <T> List<T> list(Supplier<List<T>> creator, T... elements) {
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

    @SafeVarargs
    public static <T> ArrayList<T> arrayList(T... elements) {
        return (ArrayList<T>) list(ArrayList::new, elements);
    }

    public static <T> ArrayList<T> arrayList(Iterable<T> source) {
    return (ArrayList<T>) list(ArrayList::new, source);
    }

    @SafeVarargs
    public static <T> LinkedList<T> linkedList(T... elements) {
        return (LinkedList<T>) list(LinkedList::new, elements);
    }

    public static <T> LinkedList<T> linkedList(Iterable<T> source) {
        return (LinkedList<T>) list(LinkedList::new, source);
    }

    public static int[] array(int... elements) {
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

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
