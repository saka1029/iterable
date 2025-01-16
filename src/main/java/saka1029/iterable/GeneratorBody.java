package saka1029.iterable;

public interface GeneratorBody<T> {

    void accept(GeneratorContext<T> context) throws InterruptedException;

}
