package saka1029.iterable;

import java.util.Iterator;
import java.util.function.Supplier;

public class Iterables {

    private Iterables() {
    }

    public static <T> Iterable<T> iterable(Supplier<Iterator<T>> makeIterator) {
        return () -> makeIterator.get();
    }

}
