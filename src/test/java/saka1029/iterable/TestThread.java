package saka1029.iterable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static saka1029.iterable.Iterables.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class TestThread {

    enum Status {
        EMPTY, FILLED;
    }

    class Holder<T> {
        Status status;
        T value;

        Holder(Status status, T value) {
            this.status = status;
            this.value = value;
        }

        synchronized void send(T value) throws InterruptedException {
            if (status != Status.EMPTY)
                wait();
            status = Status.FILLED;
            this.value = value;
            notify();
        }

        synchronized T receive() throws InterruptedException {
            if (status != Status.FILLED)
                wait();
            T result = value;
            status = Status.EMPTY;
            notify();
            return result;
        }
    }

    @Test
    public void testProducerConsumerTwoThread() throws InterruptedException {
        Holder<Integer> holder = new Holder<>(Status.EMPTY, -1);
        Thread producer = Thread.ofVirtual().start(() -> {
            try {
                for (int i = 0; i < 10; ++i) {
                    System.out.println("producer: send " + i);
                    holder.send(i);
                }
                holder.send(null);
                System.out.println("producer: end");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        List<Integer> received = new ArrayList<>();
        Thread consumer = Thread.ofVirtual().start(() -> {
            try {
                while (true) {
                    Integer receive = holder.receive();
                    if (receive == null)
                        break;
                    System.out.println("consumer: receive " + receive);
                    received.add(receive);
                }
                System.out.println("consumer: end " + received);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        producer.join();
        consumer.join();
        assertEquals(list(range(0, 10, 1)), received);
    }

    @Test
    public void testProducerConsumerOneThread() throws InterruptedException {
        Holder<Integer> holder = new Holder<>(Status.EMPTY, -1);
        Thread producer = Thread.ofVirtual().start(() -> {
            try {
                for (int i = 0; i < 10; ++i) {
                    System.out.println("producer: send " + i);
                    holder.send(i);
                }
                holder.send(null);
                System.out.println("producer: end");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        List<Integer> received = new ArrayList<>();
        try {
            while (true) {
                Integer receive = holder.receive();
                if (receive == null)
                    break;
                System.out.println("consumer: receive " + receive);
                received.add(receive);
            }
            System.out.println("consumer: end " + received);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        producer.join();
        assertEquals(list(range(0, 10, 1)), received);
    }

    @Test
    public void testInterrupt() {
        Thread t = new Thread(() -> {
            try {
                new Object() {
                    synchronized void func() throws InterruptedException {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            // catchした時点ではisInterrupted()はfalse
                            assertFalse(Thread.currentThread().isInterrupted());
                            // 再度、割り込みフラグをセットする。
                            Thread.currentThread().interrupt();
                            throw e;
                        }
                    }
                }.func();
                // 自前の割り込みフラグがセットされていたら最終処理はスキップする。
                assertTrue(Thread.currentThread().isInterrupted());
                if (!Thread.currentThread().isInterrupted()) {
                    System.out.println("final process");
                }
            } catch (InterruptedException e) {
                // catchした時点ではisInterrupted()はtrue
                assertTrue(Thread.currentThread().isInterrupted());
            }
        });
        t.start();
        t.interrupt();
        assertTrue(t.isInterrupted());
        System.out.println("done");
        try {
            assertTrue(t.isInterrupted());
            t.join();
        } catch (InterruptedException e) {
            System.out.println("t interrupted");
        }
    }

    @Test
    public void testThreadLocal() {
        ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
        for (int i = 0; i < 3; ++i) {
            int number = i;
            Thread t = new Thread(() -> {
                threadLocal.set(number);
                System.out.println(Thread.currentThread().getName() + " : " + threadLocal.get());
            });
            t.start();
        }
    }
}

