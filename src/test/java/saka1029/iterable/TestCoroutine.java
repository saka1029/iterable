package saka1029.iterable;

import static org.junit.Assert.assertEquals;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Test;

public class TestCoroutine {

    static class CoroutineRunner<T> implements Closeable {

        final int queSize;
        final Thread thread;
        final Queue<T> que = new LinkedList<>();

        CoroutineRunner(int queSize, CoroutineBody<T> body) {
            if (body == null)
                throw new IllegalArgumentException("body");
            Runnable runnable = () -> {
                try {
                    body.accept(this);
                    this.yield(null);
                } catch (InterruptedException e) {
                    System.out.println("CoroutineRunner: body interrupted");
                }
            };
            this.queSize = queSize;
            this.thread = new Thread(runnable);
            this.thread.start();
        }

        @Override
        public void close() {
            System.out.println("CoroutineRunner.close()");
            thread.interrupt();
        }

        synchronized void yield(T newValue) throws InterruptedException {
            System.out.println("CoroutineRunner.yield: enter " + newValue);
            if (thread.isInterrupted())
                throw new InterruptedException();
            while (que.size() >= queSize)
                wait();
            System.out.println("CoroutineRunner.yield: add " + newValue);
            que.add(newValue);
            notify();
        }

        synchronized T take() {
            System.out.println("CoroutineRunner.take: enter ");
            if (!thread.isAlive() && que.size() <= 0)
                throw new NoSuchElementException("No yield element");
            while (que.size() <= 0)
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            T result = que.remove();
            System.out.println("CoroutineRunner.take: remove " + result);
            notify();
            return result;
        }
    }

    interface CoroutineBody<T> {
        void accept(CoroutineRunner<T> coroutine) throws InterruptedException;
    }

    static class Coroutine<T> implements Iterable<T>, Closeable {

        int queSize = 4;
        CoroutineBody<T> body = null;
        List<CoroutineRunner<T>> runners = new ArrayList<>();

        public Coroutine<T> body(CoroutineBody<T> body) {
            this.body = body;
            return this;
        }

        public Coroutine<T> queSize(int queSize) {
            this.queSize = queSize;
            return this;
        }

        @Override
        public void close() {
            for (CoroutineRunner<T> e : runners)
                e.close();
        }

        private CoroutineRunner<T> newRunner() {
            if (body == null)
                throw new IllegalStateException("No body.  Call body() first");
            CoroutineRunner<T> runner =  new CoroutineRunner<>(queSize, body);
            runners.add(runner);
            return runner;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<>() {
                CoroutineRunner<T> runner = newRunner();
                T next = null;
                boolean hasNext = advance();

                private boolean advance() {
                    return (next = runner.take()) != null;
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

        public Stream<T> stream() {
            return StreamSupport.stream(this.spliterator(), false);
        }
    }

    @Test
    public void testIterator() {
        try (Coroutine<Integer> coroutine = new Coroutine<>()) {
            coroutine.body(c -> {
                c.yield(1);
                c.yield(2);
            });
            List<Integer> list = new ArrayList<>();
            for (var i : coroutine)
                list.add(i);
            assertEquals(List.of(1, 2), list);
        }
    }

    @Test
    public void testStream() {
        try (Coroutine<Integer> coroutine = new Coroutine<>()) {
            coroutine.body(c -> {
                c.yield(1);
                c.yield(2);
            });
            assertEquals(List.of(1, 2), coroutine.stream().toList());
        }
    }

    @Test
    public void testFibonacciStream() {
        try (Coroutine<Integer> coroutine = new Coroutine<>()) {
            coroutine.body(c -> {
                int a = 0, b = 1;
                while (true) {
                    c.yield(a);
                    int temp = a + b;
                    a = b;
                    b = temp;
                }
            });
            assertEquals(List.of(0, 1, 1, 2, 3, 5, 8),
                coroutine.stream()
                    .limit(7)
                    .toList());
        }
    }

}
