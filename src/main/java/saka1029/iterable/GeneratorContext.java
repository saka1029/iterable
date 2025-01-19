package saka1029.iterable;

import java.io.Closeable;
import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;

public class GeneratorContext<T> implements Closeable {

    final int queSize;
    final Thread thread;
    final Queue<T> que = new LinkedList<>();

    GeneratorContext(int queSize, GeneratorBody<T> body) {
        if (body == null)
            throw new IllegalArgumentException("body");
        Runnable runnable = () -> {
            try {
                body.accept(this);
                this.yield(null);
            } catch (InterruptedException e) {
                System.out.println("GeneratorContext: body interrupted");
            }
            System.out.println("GeneratorContext: body end");
        };
        this.queSize = queSize;
        this.thread = new Thread(runnable);
        this.thread.start();
    }

    @Override
    public void close() {
        System.out.println("GeneratorContext.close()");
        thread.interrupt();
    }

    public synchronized void yield(T newValue) throws InterruptedException {
        System.out.println("GeneratorContext.yield: enter " + str(newValue));
        if (thread.isInterrupted())
            throw new InterruptedException("GeneratorContext.yield: interrupted");
        while (que.size() >= queSize)
            wait();
        System.out.println("GeneratorContext.yield: add " + str(newValue));
        que.add(newValue);
        notify();
    }

    synchronized T take() {
        System.out.println("GeneratorContext.take: enter isAlive=" + thread.isAlive() + " que.size=" + que.size());
        if (!thread.isAlive()) {
            if (que.size() <= 0)
                throw new NoSuchElementException("No yield element");
        } else
            while (que.size() <= 0)
                try {
                    System.out.println("GeneratorContext.take: wait ");
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        T result = que.remove();
        System.out.println("GeneratorContext.take: remove " + str(result));
        notify();
        return result;
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
}
