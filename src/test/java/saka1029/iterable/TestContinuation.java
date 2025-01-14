package saka1029.iterable;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.logging.Logger;
import org.junit.Test;
import saka1029.Common;

public class TestContinuation {

    static final Logger logger = Common.logger(TestContinuation.class);

    interface CoroutineBody<T> {
        void accept(Coroutine<T> coroutine) throws InterruptedException;
    }

    interface CoroutineRun {
        void run() throws InterruptedException;
    }

    static class Coroutine<T> implements Closeable {

        final int size = 4;
        final Queue<T> que = new LinkedList<>();
        Thread thread;

        public void start(CoroutineRun body) {
            que.clear();
            Runnable runnable = () -> {
                try {
                    body.run();
                } catch (InterruptedException e) {
                    logger.info("thread end");
                }
            };
            thread = new Thread(runnable);
            thread.start();
        }

        public void start(CoroutineBody<T> body) {
            que.clear();
            Runnable runnable = () -> {
                try {
                    body.accept(this);
                } catch (InterruptedException e) {
                    logger.info("thread end");
                }
            };
            thread = new Thread(runnable);
            thread.start();
        }

        public synchronized void yield(T newValue) throws InterruptedException {
            if (Thread.currentThread().isInterrupted())
                throw new InterruptedException();
            while (que.size() >= size)
                wait();
            que.add(newValue);
            notify();
        }

        public synchronized T take() {
            if (!thread.isAlive() && que.size() <= 0)
                throw new NoSuchElementException("No yield element");
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
        public void close() {
            thread.interrupt();
        }
    }

    @Test
    public void testCoroutine() {
        try (var coroutine = new Coroutine<>()) {
            coroutine.start(c -> {
                for (int i = 0; true; ++i)
                    c.yield(i);
            });
            logger.info("take: " + coroutine.take());
        }
    }

    @Test
    public void testCoroutineRun() {
        try (var coroutine = new Coroutine<>()) {
            coroutine.start(() -> {
                for (int i = 0; true; ++i)
                    coroutine.yield(i);
            });
            logger.info("take: " + coroutine.take());
        }
    }

    @Test
    public void testTakeTooMatch() {
        try (var coroutine = new Coroutine<>()) {
            coroutine.start(() -> {
                coroutine.yield(3);
            });
            logger.info("take: " + coroutine.take());
            logger.info("take: " + coroutine.take());
        }
    }
}
