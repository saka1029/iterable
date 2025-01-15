package saka1029.iterable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

    static class CoroutineContext<T> implements Closeable {

        final int queSize;
        final Thread thread;
        final Queue<T> que = new LinkedList<>();

        CoroutineContext(int queSize, CoroutineBody<T> body) {
            if (body == null)
                throw new IllegalArgumentException("body");
            Runnable runnable = () -> {
                try {
                    body.accept(this);
                    this.yield(null);
                } catch (InterruptedException e) {
                    System.out.println("CoroutineRunner: body interrupted");
                }
                System.out.println("CoroutineRunner: body end");
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

        public synchronized void yield(T newValue) throws InterruptedException {
            System.out.println("CoroutineRunner.yield: enter " + newValue);
            if (thread.isInterrupted())
                throw new InterruptedException();
            while (que.size() >= queSize)
                wait();
            System.out.println("CoroutineRunner.yield: add " + newValue);
            que.add(newValue);
            notify();
        }

        private synchronized T take() {
            System.out.println("CoroutineRunner.take: enter isAlive=" + thread.isAlive() + " que.size=" + que.size());
            if (!thread.isAlive())
                if (que.size() <= 0)
                    throw new NoSuchElementException("No yield element");
                else
                    return que.remove();
            while (que.size() <= 0)
                try {
                    System.out.println("CoroutineRunner.take: wait ");
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
        void accept(CoroutineContext<T> coroutine) throws InterruptedException;
    }

    static class Coroutine<T> implements Iterable<T>, Closeable {

        int queSize = 4;
        CoroutineBody<T> body = null;
        List<CoroutineContext<T>> runners = new ArrayList<>();

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
            for (CoroutineContext<T> e : runners)
                e.close();
        }

        private CoroutineContext<T> context() {
            if (body == null)
                throw new IllegalStateException("No body.  Call body() first");
            CoroutineContext<T> runner =  new CoroutineContext<>(queSize, body);
            runners.add(runner);
            return runner;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<>() {
                CoroutineContext<T> context = context();
                T next = null;
                boolean hasNext = advance();

                private boolean advance() {
                    return (next = context.take()) != null;
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
        try (Coroutine<Integer> fibonacci = new Coroutine<>()) {
            fibonacci.body(c -> {
                int a = 0, b = 1;
                while (true) {
                    c.yield(a);
                    int temp = a + b;
                    a = b;
                    b = temp;
                }
            });
            assertEquals(List.of(0, 1, 1, 2, 3, 5, 8),
                fibonacci.stream()
                    .limit(7)
                    .toList());
        }
    }

    @Test
    public void testNoSuchElement() {
        try (Coroutine<Integer> coroutine = new Coroutine<>()) {
            coroutine.body(c -> {
                c.yield(0);
                c.yield(1);
            });
            CoroutineContext<Integer> cc = coroutine.context();
            System.out.println(">>> " + cc.take());
            System.out.println(">>> " + cc.take());
            System.out.println(">>> " + cc.take());
            try {
                System.out.println(">>> " + cc.take());
                fail();
            } catch (NoSuchElementException x) {
            }
        }
    }

}
