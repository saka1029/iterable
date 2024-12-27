package saka1029.iterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Iterables {

    private Iterables() {
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

    public static <T> List<T> fixedSizeList(int n) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < n; ++i)
            list.add(null);
        return list;
    }

    public static <T> Iterable<List<T>> map(List<T> list, Iterable<int[]> source) {
        return map(ints -> {
            List<T> result = fixedSizeList(ints.length);
            for (int i : ints)
                result.set(i, list.get(i));
            return result;
        }, source);
    }

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
