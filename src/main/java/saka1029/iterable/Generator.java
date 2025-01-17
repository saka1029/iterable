package saka1029.iterable;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Generator<T> implements Iterable<T>, Closeable {

        int queSize = 4;
        final GeneratorBody<T> body;
        List<GeneratorContext<T>> runners = new ArrayList<>();

        public Generator(GeneratorBody<T> body) {
            this.body = body;
        }

        public Generator<T> queSize(int queSize) {
            this.queSize = queSize;
            return this;
        }

        @Override
        public void close() {
            for (GeneratorContext<T> e : runners)
                e.close();
        }

        private GeneratorContext<T> context() {
            if (body == null)
                throw new IllegalStateException("No body.  Call body() first");
            GeneratorContext<T> runner =  new GeneratorContext<>(queSize, body);
            runners.add(runner);
            return runner;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<>() {
                GeneratorContext<T> context = context();
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
}
