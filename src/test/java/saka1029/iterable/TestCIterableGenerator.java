package saka1029.iterable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class TestCIterableGenerator {

    interface CIterator<T> extends Iterator<T> {
        default void close() {}
    }

    interface CIterable<T> extends Iterable<T> {
        CIterator<T> iterator();
    }

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
            };
        }

        public synchronized void yield(T newValue) throws InterruptedException {
            while (que.size() >= capacity)
                try {
                    wait();
                } catch (InterruptedException e) {
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
                    coroutine.interrupt();
                    next = null;
                }
            };
        }
    }

    static class CArrayList<T> extends ArrayList<T> implements CIterable<T> {

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
}
