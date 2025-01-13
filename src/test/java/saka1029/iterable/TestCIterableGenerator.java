package saka1029.iterable;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import org.junit.Test;
import saka1029.Common;

public class TestCIterableGenerator {

    static final Logger logger = Common.logger(TestCIterableGenerator.class);

    interface CIterator<T> extends Iterator<T> {
        default void close() {}
    }

    interface CIterable<T> extends Iterable<T> {
        CIterator<T> iterator();
    }

    // creator

    static class Generator<T> implements CIterable<T> {

        // static class Holder<T> {
        //     final T value;
        //     final boolean filled;
        //     Holder(T value, boolean filled) {
        //         this.value = value;
        //         this.filled = filled;
        //     }
        // }

        final int capacity = 8;
        final Queue<T> que = new LinkedList<>();
        final Runnable runnable;

        public Generator(Consumer<Generator<T>> generator) {
            this.runnable = () -> {
                generator.accept(this);
                if (!Thread.currentThread().isInterrupted())
                    try {
                        this.yield(null);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                logger.info("Generator: thread end");
            };
        }

        public synchronized void yield(T newValue) throws InterruptedException {
            while (que.size() >= capacity)
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.info("Generator: yield inerrupted");
                    Thread.currentThread().interrupt();
                    throw e;
                }
            que.add(newValue);
            notify();
        }

        private synchronized T take() {
            while (que.size() <= 0)
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            T result = que.remove();
            notify();
            return result;
        }

        @Override
        public CIterator<T> iterator() {
            Thread coroutine = new Thread(runnable);
            coroutine.start();
            return new CIterator<>() {
                T next = take();

                @Override
                public boolean hasNext() {
                    return next != null;
                }

                @Override
                public T next() {
                    T result = next;
                    next = take();
                    return result;
                }

                @Override
                public void close() {
                    logger.info("Generator: close inerrupt");
                    coroutine.interrupt();
                    next = null;
                }
            };
        }
    }

    static class CArrayList<T> extends ArrayList<T> implements CIterable<T> {

        public CArrayList() {
        }

        @SuppressWarnings("unchecked")
        public CArrayList(T... elements) {
            this();
            for (T e : elements)
                add(e);
        }

        @Override
        public CIterator<T> iterator() {
            return new CIterator<>() {
                int index = 0;

                @Override
                public boolean hasNext() {
                    return index < size();
                }

                @Override
                public T next() {
                    return get(index++);
                }
            };
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> CIterable<T> listOf(T... elements) {
        return new CArrayList<>(elements);
    }

    // converter

    public static <T, U> CIterable<U> map(Function<T, U> mapper, CIterable<T> source) {
        return () -> new CIterator<>() {
            CIterator<T> iterator = source.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public U next() {
                return mapper.apply(iterator.next());
            }

            @Override
            public void close() {
                logger.info("map: closed called");
                iterator.close();
            }
        };
    }

    public static <T> CIterable<T> filter(Predicate<T> predicate, CIterable<T> source) {
        return () -> new CIterator<>() {

            final CIterator<T> iterator = source.iterator();
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

            @Override
            public void close() {
                logger.info("filter: closed called");
                iterator.close();
            }
        };
    }

    // terminator

    public static <T> List<T> list(CIterable<T> source) {
        CArrayList<T> list = new CArrayList<>();
        for (T e : source)
            list.add(e);
        return list;
    }

    @Test
    public void testListOf() {
        logger.info("*** " + Common.methodName());
        assertEquals(List.of(2, 3, 4), listOf(2, 3, 4));
    }

    @Test
    public void testMap() {
        logger.info("*** " + Common.methodName());
        assertEquals(List.of(20, 30, 40), list(map(e -> e * 10, listOf(2, 3, 4))));
    }

    @Test
    public void testFilter() {
        logger.info("*** " + Common.methodName());
        assertEquals(List.of(1, 3, 5), list(filter(e -> e % 2 != 0, listOf(0, 1, 2, 3, 4, 5))));
    }
}
