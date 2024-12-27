package saka1029.iterable;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Iterables {

    private Iterables() {
    }

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T> Iterable<T> iterable(Supplier<Iterator<T>> makeIterator) {
        return () -> makeIterator.get();
    }

}
