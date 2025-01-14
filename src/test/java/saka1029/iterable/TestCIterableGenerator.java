package saka1029.iterable;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.logging.Logger;
import org.junit.Test;
import saka1029.Common;

public class TestCIterableGenerator {

    static final Logger logger = Common.logger(TestCIterableGenerator.class);

    interface CIterator<T> extends Iterator<T> {
        void stop();
    }

    interface CIterable<T> extends Iterable<T> {
        CIterator<T> iterator();
    }

    interface CList<T> extends List<T>, CIterable<T> {
    }

    interface GeneratorBody<T> {
        void accept(Generator<T> body) throws InterruptedException;
    }

    static class Generator<T> implements CIterable<T> {

        final int capacity = 10;
        final Queue<T> que = new LinkedList<>();
        final Runnable runnable;

        public Generator(GeneratorBody<T> body) {
            this.runnable = () -> {
                try {
                    body.accept(this);
                    this.yield(null);
                } catch (InterruptedException e) {
                    logger.info("Generator.runnable: interrupted");
                }
                logger.info("Generator.runnable: end");
            };
        }

        public synchronized void yield(T newValue) throws InterruptedException {
            logger.info("Generator.yield enter " + newValue);
            if (Thread.currentThread().isInterrupted())
                throw new InterruptedException("Generator.yield: interruped");
            while (que.size() >= capacity)
                wait();
            logger.info("Generator.yield add " + newValue);
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
            logger.info("Generator.take remove " + result);
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
                public void stop() {
                    logger.info("Generator.stop");
                    coroutine.interrupt();
                    next = null;
                }
            };
        }
    }

    static class CArrayList<T> extends ArrayList<T> implements CList<T> {

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

                @Override
                public void stop() {
                }
            };
        }
    }

    // creator

    public static <T> Generator<T> generate(GeneratorBody<T> generator) {
        return new Generator<>(generator);
    }

    @SuppressWarnings("unchecked")
    public static <T> CIterable<T> listOf(T... elements) {
        return new CArrayList<>(elements);
    }

    public static <T> CIterable<T> iterable(Iterable<T> source) {
        return () -> new CIterator<>() {
            Iterator<T> iterator = source.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }

            @Override
            public void stop() {
            }
        };
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
            public void stop() {
                logger.info("map: stop");
                iterator.stop();
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
            public void stop() {
                logger.info("filter: stop");
                iterator.stop();
            }
        };
    }

    public static <T> CIterable<T> limit(int size, CIterable<T> source) {
        return () -> new CIterator<>() {
            CIterator<T> iterator = source.iterator();
            int count = 0;

            @Override
            public boolean hasNext() {
                if (iterator.hasNext() && count < size)
                    return true;
                stop();
                return false;
            }

            @Override
            public T next() {
                ++count;
                return iterator.next();
            }

            @Override
            public void stop() {
                logger.info("limit: stop");
                iterator.stop();
            }
        };
    }

    public static <T> CIterable<T> sort(Comparator<T> comparator, CIterable<T> source) {
        CList<T> list = list(source);
        Collections.sort(list, comparator);
        return list;
    }

    public static <T extends Comparable<T>> CIterable<T> sort(CIterable<T> source) {
        return sort(Comparator.naturalOrder(), source);
    }

    public static <T> CIterable<T> reverse(CIterable<T> source) {
        CList<T> list = list(source);
        Collections.reverse(list);
        return list;
    }

    public static <T, U, V> CIterable<V> zip(BiFunction<T, U, V> mapper,
            CIterable<T> s1, CIterable<U> s2) {
        return () -> new CIterator<>() {
            CIterator<T> i1 = s1.iterator();
            CIterator<U> i2 = s2.iterator();

            @Override
            public boolean hasNext() {
                if (i1.hasNext() && i2.hasNext())
                    return true;
                stop();
                return false;
            }

            @Override
            public V next() {
                return mapper.apply(i1.next(), i2.next());
            }

            @Override
            public void stop() {
                i1.stop();
                i2.stop();
            }
        };
    }

    // terminator

    public static <T> CList<T> list(CIterable<T> source) {
        CList<T> list = new CArrayList<>();
        for (T e : source)
            list.add(e);
        return list;
    }

    public static <T> T[] array(IntFunction<T[]> constructor, CIterable<T> source) {
        CList<T> list = list(source);
        return list.toArray(constructor);
    }

    public static <T> boolean allMatch(Predicate<T> predicate, CIterable<T> source) {
        for (CIterator<T> it = source.iterator(); it.hasNext(); )
            if (!predicate.test(it.next())) {
                it.stop();
                return false;
            }
        return true;
    }

    public static <T> boolean anyMatch(Predicate<T> predicate, CIterable<T> source) {
        for (CIterator<T> it = source.iterator(); it.hasNext(); )
            if (predicate.test(it.next())) {
                it.stop();
                return true;
            }
        return false;
    }

    @Test
    public void testGenerate() {
        assertEquals(List.of(0, 1, 2),
            list(generate(g -> {
                g.yield(0);
                g.yield(1);
                g.yield(2);
            })));
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

    @Test
    public void testLimit() {
        logger.info("*** " + Common.methodName());
        assertEquals(List.of(0, 1, 2), list(limit(3, listOf(0, 1, 2, 3, 4, 5))));
    }

    @Test
    public void testLimitMap() {
        logger.info("*** " + Common.methodName());
        assertEquals(List.of(1, 11, 21),
            list(
                limit(3,
                    map(e -> e + 1,
                        map(e -> e * 10,
                            listOf(0, 1, 2, 3, 4, 5))))));
    }

    @Test
    public void testLimitGenerate() {
        logger.info("*** " + Common.methodName());
        assertEquals(List.of(1, 11, 21),
            list(
                limit(3,
                    map(e -> e + 1,
                        map(e -> e * 10,
                            generate((Generator<Integer> g) -> {
                                g.yield(0);
                                g.yield(1);
                                g.yield(2);
                                g.yield(3);
                                g.yield(4);
                                g.yield(5);
                                g.yield(6);
                                g.yield(7);
                                g.yield(8);
                                g.yield(9);
                            }))))));
    }

    @Test
    public void testLimitGenerateLoop() {
        logger.info("*** " + Common.methodName());
        assertEquals(List.of(1, 11, 21),
            list(
                limit(3,
                    map(e -> e + 1,
                        map(e -> e * 10,
                            generate((Generator<Integer> g) -> {
                                for (int i = 0; i < 100; ++i)
                                    g.yield(i);
                            }))))));
    }

    @Test
    public void testLimitFibonacci() {
        logger.info("*** " + Common.methodName());
        assertEquals(List.of(0, 1, 1, 2, 3, 5),
            list(
                limit(6,
                    generate((Generator<Integer> g) -> {
                        int a = 0, b = 1;
                        while (true) {
                            logger.info("fibonacci: yield " + a);
                            g.yield(a);
                            int c = a + b;
                            a = b;
                            b = c;
                        }
                    }))));
    }

    @Test
    public void testAllMatch() {
        logger.info("*** " + Common.methodName());
        assertFalse(allMatch(i -> i % 3 == 0, listOf(0, 1, 2, 3)));
        assertFalse(allMatch(i -> i % 3 == 0, map(i -> i + 1, listOf(0, 1, 2, 3))));
        assertTrue(allMatch(i -> i % 3 == 0, listOf(0, 3, 6, 9)));
        assertTrue(allMatch(i -> i % 3 == 0, map(i -> i + 1, listOf(2, 5, 8))));
        assertFalse(allMatch(i -> i % 3 == 0, map(i -> i + 1, listOf(0, 3, 6, 9))));
    }

    @Test
    public void testAnyMatch() {
        logger.info("*** " + Common.methodName());
        assertTrue(anyMatch(i -> i % 3 == 0, listOf(0, 1, 2, 3)));
        assertTrue(anyMatch(i -> i % 3 == 0, listOf(0, 3, 6, 9)));
        assertFalse(anyMatch(i -> i % 3 == 0, listOf(1, 4, 7, 10)));
    }

    @Test
    public void testSort() {
        logger.info("*** " + Common.methodName());
        assertEquals(listOf(0, 1, 2, 3, 4), sort(Comparator.naturalOrder(), listOf(2, 3, 1, 4, 0)));
        assertEquals(listOf(4, 3, 2, 1, 0), sort(Comparator.reverseOrder(), listOf(2, 3, 1, 4, 0)));
        assertEquals(listOf(0, 1, 2, 3, 4), sort(Comparator.comparing(Function.identity()), listOf(2, 3, 1, 4, 0)));
        assertEquals(listOf(0, 1, 2, 3, 4), sort(Integer::compare, listOf(2, 3, 1, 4, 0)));
        assertEquals(listOf(0, 1, 2, 3, 4), sort(listOf(2, 3, 1, 4, 0)));
    }

    @Test
    public void testReverse() {
        assertEquals(listOf(0, 4, 1, 3, 2), reverse(listOf(2, 3, 1, 4, 0)));
    }

    @Test
    public void testArray() {
        assertArrayEquals(new Integer[] {0, 1, 2}, array(Integer[]::new, listOf(0, 1, 2)));
    }
}
