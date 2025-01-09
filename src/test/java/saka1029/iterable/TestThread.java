package saka1029.iterable;

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
    public void testProducerConsumerSimple() throws InterruptedException {
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
        producer.join(); consumer.join();
    }
}

