package saka1029.iterable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.Closeable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Test;

import saka1029.Common;
import saka1029.iterable.TestNewGenerator.Generator.Context;

public class TestNewGenerator {

    public static class Generator<T> implements Iterable<T>, Closeable {

        static final Logger logger = Common.logger(Generator.class);

        static void info(String s) {
            logger.info(s);
        }

        public interface Body<T> {
            void accept(Context<T> context) throws InterruptedException;
        }

        public static class Context<T> implements Closeable {

            private final int queSize;
            private final Thread thread;
            private final Queue<T> que = new LinkedList<>();
            private boolean done = false;

            Context(int queSize, Body<T> body) {
                Runnable runnable = () -> {
                    try {
                        body.accept(this);
                        // this.yield(null);
                        bodyEnd();
                    } catch (InterruptedException e) {
                        info("Generator.Context: body interrupted");
                    }
                    info("Generator.Context: body end");
                };
                this.queSize = queSize;
                this.thread = new Thread(runnable);
                this.thread.start();
            }

            @Override
            public void close() {
                info("Generator.Context.close()");
                thread.interrupt();
            }

            synchronized void bodyEnd() {
                this.done = true;
            }

            public synchronized boolean done() {
                return this.done;
            }

            public synchronized void yield(T newValue) throws InterruptedException {
                info("Generator.Context.yield: enter " + str(newValue));
                if (thread.isInterrupted()) {
                    info("Generator.Context.yield: throw InterruptedException");
                    throw new InterruptedException("Generator.Context.yield: interrupted");
                }
                while (que.size() >= queSize) {
                    info("Generator.Context.yield: wait");
                    wait();
                }
                info("Generator.Context.yield: add " + str(newValue));
                que.add(newValue);
                notify();
            }

            synchronized T take() {
                info("Generator.Context.take: enter isAlive=" + thread.isAlive() + " done=" + done() + " que.size=" + que.size());
                if (done()) {
                    if (que.size() <= 0) {
                        info("Generator.Context.take: throw NoSuchElementException");
                        throw new NoSuchElementException("No yield element");
                    }
                } else
                    while (que.size() <= 0)
                        try {
                            info("Generator.Context.take: wait ");
                            wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                T result = que.remove();
                // if (result == null)
                //     takeNull = true;
                info("Generator.Context.take: remove " + str(result));
                notify();
                return result;
            }
        }

        static String str(Object obj) {
            if (obj == null)
                return "null";
            if (!obj.getClass().isArray())
                return Objects.toString(obj);
            StringBuilder sb = new StringBuilder("[");
            int size = Array.getLength(obj);
            if (size > 0)
                sb.append(str(Array.get(obj, 0)));
            for (int i = 1; i < size; ++i)
                sb.append(", ").append(str(Array.get(obj, i)));
            return sb.append("]").toString();
        }

        int queSize = 4;
        final Body<T> body;
        List<Context<T>> runners = new ArrayList<>();

    Generator(Body<T> body) {
        if (body == null)
            throw new IllegalArgumentException("body");
        this.body = body;
    }

        public static <T> Generator<T> of(Body<T> body) {
            return new Generator<>(body);
        }

        public Generator<T> queSize(int queSize) {
            if (queSize <= 0)
                throw new IllegalArgumentException("queSize");
            this.queSize = queSize;
            return this;
        }

        @Override
        public void close() {
            for (Context<T> e : runners)
                e.close();
        }

        Context<T> context() {
            if (body == null)
                throw new IllegalStateException("No body.  Call body() first");
            Context<T> runner = new Context<>(queSize, body);
            runners.add(runner);
            return runner;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<>() {
                Context<T> context = context();
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
            return StreamSupport.stream(spliterator(), false);
        }
    }

    @Test
    public void testGeneratorTake() {
        try (Generator<Integer> g = Generator.of(c -> {
            c.yield(2);
            c.yield(null);
            c.yield(1);
            c.yield(null);
        })) {
            Context<Integer> c = g.context();
            assertEquals(2, (int)c.take());
            assertNull(c.take());
            assertEquals(1, (int)c.take());
            assertNull(c.take());
            try {
                assertNull(c.take());
                fail();
            } catch (NoSuchElementException e) {
                assertEquals("No yield element", e.getMessage());
            }
        }
    }

    @Test
    public void testGeneratorBreak() {
        try (Generator<Integer> g = Generator.of(c -> {
            for (int i = 0; i < 1000; ++i)
                c.yield(i);
        })) {
            Context<Integer> c = g.context();
            assertEquals(0, (int)c.take());
            assertEquals(1, (int)c.take());
        }
    }
}
