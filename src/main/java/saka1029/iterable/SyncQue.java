package saka1029.iterable;

import java.util.LinkedList;
import java.util.Queue;

public class SyncQue<T> {

    final int capacity;
    private final Queue<T> que = new LinkedList<>();

    SyncQue(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void add(T newValue) {
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

    public synchronized T remove() {
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

    public static Thread start(Runnable runnable) {
        Thread t = new Thread(runnable);
        t.start();
        return t;
    }
}
