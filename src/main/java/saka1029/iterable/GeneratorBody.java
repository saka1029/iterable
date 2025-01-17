package saka1029.iterable;

/**
 * Generatorの本体を定義するためのインタフェースです。
 */
public interface GeneratorBody<T> {

    void accept(GeneratorContext<T> context) throws InterruptedException;

}
