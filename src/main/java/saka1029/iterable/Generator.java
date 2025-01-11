package saka1029.iterable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class Generator<T> implements Iterable<T> {

    final int capacity = 10;
    private final Queue<T> que = new LinkedList<>();
    final Runnable runnable;

    public Generator(Consumer<Generator<T>> generator) {
        this.runnable = () -> {
            generator.accept(this);
            this.yield(null);
        };
    }

    public static <T> Generator<T> of(Consumer<Generator<T>> generator) {
        return new Generator<>(generator);
    }

    public synchronized void yield(T newValue) {
        while (que.size() >= capacity)
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        que.add(newValue);
        notify();
    }

    private synchronized T take() {
        while (que.size() <= 0)
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        T result = que.remove();
        notify();
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        Thread t = new Thread(runnable);
        t.start();
        return new Iterator<>() {
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
        };
    }
}
