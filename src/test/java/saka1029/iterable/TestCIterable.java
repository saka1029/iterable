package saka1029.iterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;

public class TestCIterable {

    interface CIterator<T> extends Iterator<T> {
        default void close() {}
    }

    interface CIterable<T> extends Iterable<T> {
        CIterator<T> iterator();
    }

    static class CArrayList<T> extends ArrayList<T> implements CIterable<T> {

        @SuppressWarnings("unchecked")
        public CArrayList(T... elements) {
            for (T e : elements)
                add(e);
        }

        @Override
        public CIterator<T> iterator() {
            return new CIterator<>() {
                int i = 0;

                @Override
                public boolean hasNext() {
                    return i < size();
                }

                @Override
                public T next() {
                    return get(i++);
                }

                @Override
                public void close() {
                    CIterator.super.close();
                }
            };
        }
    }

    @Test
    public void testCIterable() {
        CIterable<Integer> ci = () -> new CIterator<>() {
            int[] a = {1, 2, 3};
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < a.length;
            }

            @Override
            public Integer next() {
                return a[i++];
            }

            @Override
            public void close() {
                System.out.println("closed");
            }
        };

        for (int i : ci)
            System.out.println(i);
        System.out.println(ci.iterator().getClass().getName());
        CIterator<Integer> iter = ci.iterator();
        iter.close();
    }

    @Test
    public void testCArrayList() {
        List<Integer> list = new CArrayList<>(1, 2, 3);
        System.out.println(list);
        for (int i : list)
            System.out.println(i);
    }

}
