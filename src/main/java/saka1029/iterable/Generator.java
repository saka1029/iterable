package saka1029.iterable;

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
import saka1029.Common;

public class Generator<T> implements Iterable<T>, Closeable {

    static final Logger logger = Common.logger(Generator.class);
    static void info(String s) {
        logger.info(s);
    }

    /**
     * Generatorの本体を定義するためのインタフェースです。
     */
    public interface Body<T> {
        void accept(Context<T> context) throws InterruptedException;
    }

    public static class Context<T> implements Closeable {

        final int queSize;
        final Thread thread;
        final Queue<T> que = new LinkedList<>();

        Context(int queSize, Body<T> body) {
            Runnable runnable = () -> {
                try {
                    body.accept(this);
                    this.yield(null);
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

        public synchronized void yield(T newValue) throws InterruptedException {
            info("Generator.Context.yield: enter " + str(newValue));
            if (thread.isInterrupted())
                throw new InterruptedException("Generator.Context.yield: interrupted");
            while (que.size() >= queSize)
                wait();
            info("Generator.Context.yield: add " + str(newValue));
            que.add(newValue);
            notify();
        }

        synchronized T take() {
            info("Generator.Context.take: enter isAlive=" + thread.isAlive()
                    + " que.size=" + que.size());
            if (!thread.isAlive()) {
                if (que.size() <= 0)
                    throw new NoSuchElementException("No yield element");
            } else
                while (que.size() <= 0)
                    try {
                        info("Generator.Context.take: wait ");
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
            T result = que.remove();
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
