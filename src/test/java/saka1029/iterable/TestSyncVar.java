package saka1029.iterable;

public class TestSyncVar {

    public enum SyncVarStatus {
        EMPTY, SET, CLOSED
    }

    public static class SyncVar<T> {
        SyncVarStatus status = SyncVarStatus.EMPTY;
        T value = null;

        SyncVar(SyncVarStatus status, T value) {
            this.status = status;
            this.value = value;
        }

        public static <T> SyncVar<T> of(SyncVar<T> variable) {
            return new SyncVar<>(variable.status, variable.value);
        }

        public synchronized void set(T newValue) throws InterruptedException {
            while (status == SyncVarStatus.SET)
                wait();
            if (status == SyncVarStatus.CLOSED)
                throw new IllegalStateException("closed");
            value = newValue;
            status = SyncVarStatus.SET;
            notify();
        }

        public synchronized void close() throws InterruptedException {
            while (status == SyncVarStatus.SET)
                wait();
            if (status == SyncVarStatus.CLOSED)
                throw new IllegalStateException("closed");
            value = null;
            status = SyncVarStatus.CLOSED;
            notify();
        }

        public synchronized SyncVar<T> get() throws InterruptedException {
            while (status == SyncVarStatus.EMPTY)
                wait();
            if (status == SyncVarStatus.CLOSED)
                throw new IllegalStateException("closed");
            SyncVar<T> result = of(this);
            notify();
            return result;
        }

        public boolean isClosed() {
            return status == SyncVarStatus.CLOSED;
        }

        public T value() {
            return value;
        }

    }
}
