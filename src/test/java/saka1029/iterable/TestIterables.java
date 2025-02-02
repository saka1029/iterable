package saka1029.iterable;

import static saka1029.iterable.Iterables.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.Test;

public class TestIterables {

    @Test
    public void testList() {
        assertEquals(List.of(), listOf());
        assertEquals(List.of(0, 1, 2), listOf(0, 1, 2));
        assertEquals(List.of("a", "b"), listOf("a", "b"));
    }

    @Test
    public void testArrayOf() {
        int[][] expected = {{0, 1}, {2}, {}};
        assertArrayEquals(expected, arrayOf(arrayOf(0, 1), arrayOf(2), arrayOf()));

    }

    @Test
    public void testArrayList() {
        List<Integer> list = listOf(ArrayList::new, 0, 1, 2);
        assertEquals(List.of(0, 1, 2), list);
    }

    @Test
    public void testLinkedList() {
        List<Integer> list = listOf(LinkedList::new, 0, 1, 2);
        assertEquals(List.of(0, 1, 2), list);
    }

    @Test
    public void testRange() {
        assertEquals(listOf(0, 1, 2), list(range(0, 3, 1)));
        assertEquals(listOf(3, 2, 1), list(range(3, 0, -1)));
        assertEquals(listOf(0, 2), list(range(0, 4, 2)));
        assertEquals(listOf(4, 2), list(range(4, 0, -2)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangeIllegalStep() {
        list(range(0, 3, 0));
    }

    // @Test
    // public void testGenerate() {
    //     class Seed { int i = 0; }
    //     assertEquals(listOf(0, 1, 2),
    //         list(generate(Seed::new, seed -> seed.i < 3, seed -> seed.i++)));
    //     assertEquals(listOf(0, 1, 2),
    //         list(generate(() -> new Object() { int i = 0; },
    //             seed -> seed.i < 3, seed -> seed.i++)));
    //     assertEquals(listOf(0, 1, 2),
    //         list(limit(3, generate(() -> new Object() { int i = 0; },
    //             seed -> true, seed -> seed.i++))));
    //     Iterable<Integer> gen = generate(() -> new Object() { int i = 0; },
    //         seed -> seed.i < 3, seed ->seed.i++);
    //     assertEquals(listOf(0, 1, 2), list(gen));
    //     assertEquals(listOf(0, 1, 2), list(gen));
    // }

    @Test
    public void testStreamSupplierToIterable() {
        assertEquals(listOf(1, 2, 3), list(() -> Stream.of(1, 2, 3).iterator()));
        assertEquals(listOf(1, 2, 3), list(iterable(() -> Stream.of(1, 2, 3))));
        Iterable<Integer> s = () -> Stream.of(1, 2, 3).iterator();
        assertEquals(listOf(1, 2, 3), list(s));
        // ２回目以降も呼び出せる。
        assertEquals(listOf(1, 2, 3), list(s));
    }

    // @Test
    // public void testStreamToIterable() {
    //     assertEquals(listOf(1, 2, 3), list(iterable(Stream.of(1, 2, 3))));
    //     Iterable<Integer> stream = iterable(Stream.of(1, 2, 3));
    //     assertEquals(listOf(1, 2, 3), list(stream));
    //     try {
    //         // ２回めの呼び出しは例外を投げる。
    //         assertEquals(listOf(1, 2, 3), list(stream));
    //         fail();
    //     } catch (IllegalStateException e) {
    //     }
    // }

    @Test
    public void testLinkedListFromIterable() {
        List<Integer> list = list(LinkedList::new, listOf(0, 1, 2));
        assertEquals(List.of(0, 1, 2), list);
    }

    @Test
    public void testArray() {
        assertArrayEquals(new String[] {"a", "b", "c"},
            array(String[]::new, listOf("a", "b", "c")));
    }

    @Test
    public void testIterate() {
        assertEquals(intListOf(0, 1, 1, 2, 3, 5, 8, 13, 21, 34),
            list(
                limit(10,
                    map(a -> a[0],
                        iterate(new int[] {0, 1}, a -> new int[] {a[1], a[0] + a[1]})))));
    }

    @Test
    public void testIterateUpdate() {
        assertEquals(intListOf(0, 1, 1, 2, 3, 5, 8, 13, 21, 34),
            list(
                limit(10,
                    map(a -> a[0],
                        iterate(new int[] {0, 1},
                            a -> {
                                int t = a[0] + a[1];
                                a[0] = a[1];
                                a[1] = t;
                                return a;
                            })))));
    }

    @Test
    public void testMap() {
        assertEquals(listOf(10, 20, 30),
            list(
                map(i -> i * 10,
                    listOf(1, 2, 3))));
        assertEquals(listOf(11, 22, 33),
            list(
                zip((a, b) -> a + b,
                    listOf(1, 2, 3),
                    listOf(10, 20, 30))));
        assertEquals(listOf(11, 22, 33),
            list(
                zip((a, b) -> a + b,
                    listOf(1, 2, 3),
                    listOf(10, 20, 30, 40))));
        assertEquals(listOf(11, 22, 33),
            list(
                zip((a, b) -> a + b,
                    listOf(1, 2, 3, 4),
                    listOf(10, 20, 30))));
    }

    @Test
    public void testFlatMap() {
        assertEquals(listOf(0, 1, 2, 3),
            list(
                flatMap(identity(),
                    listOf(listOf(0, 1), listOf(2), listOf(3)))));
    }

    @Test
    public void testFilter() {
        assertEquals(listOf(1, 3),
            list(
                filter(x -> x % 2 != 0,
                    listOf(0, 1, 2, 3))));
    }

    @Test
    public void testLimit() {
        assertEquals(listOf(0, 1, 2, 3), list(limit(4, range(0, 10, 1))));
        assertEquals(listOf(0, 1, 2), list(limit(4, range(0, 3, 1))));
    }

    @Test
    public void testDistinct() {
        assertEquals(Set.of(0, 1, 2), distinct(listOf(0, 1, 1, 2, 0, 2)));
    }

    @Test
    public void testStream() {
        assertEquals(listOf(1, 2, 3), stream(listOf(1, 2, 3)).toList());
    }

    @Test
    public void testIntArray() {
        assertArrayEquals(new int[] {0, 1, 2}, intArray(intListOf(0, 1, 2)));
    }

    @Test
    public void testHashMap() {
        Map<String, Integer> map = map(s -> s, s -> s.length(), listOf("one", "two", "three"));
        assertEquals(Map.of("one", 3, "two", 3, "three", 5), map);
    }

    @Test
    public void testTreeMap() {
        Map<String, Integer> map = map(s -> s, s -> s.length(), listOf("one", "two", "three"));
        assertEquals(Map.of("one", 3, "two", 3, "three", 5), map);
    }

    @Test
    public void testMakeMap() {
        assertEquals(Map.of(0, "zero", 1, "one"),
            map(listOf(0, 1), List.of("zero", "one")));
        assertEquals(Map.of(0, "zero", 1, "one"),
            map(listOf(0, 1, 2), List.of("zero", "one")));
        assertEquals(Map.of(0, "zero", 1, "one"),
            map(listOf(0, 1), List.of("zero", "one", "two")));
    }

    @Test
    public void testAllMatch() {
        assertTrue(allMatch(n -> n < 10, listOf(0, 1, 2)));
        assertFalse(allMatch(n -> n > 10, listOf(0, 1, 2)));
    }

    @Test
    public void testAnyMatch() {
        assertTrue(anyMatch(n -> n < 10, listOf(0, 1, 2)));
        assertFalse(anyMatch(n -> n > 10, listOf(0, 1, 2)));
    }

    @Test
    public void testCount() {
        assertEquals(3, count(range(0, 3, 1)));
    }

    @Test
    public void testReduce() {
        assertEquals(10, (int) reduce(0, (a, b) -> a + b, listOf(1, 2, 3, 4)));
        assertEquals(1234, (int) reduce(0, (a, b) -> 10 * a + b, listOf(1, 2, 3, 4)));
    }

    static Iterable<Integer> primes(int max) {
        Iterable<Integer> primes = range(2, max, 1);
        Function<Integer, Predicate<Integer>> sieve = n -> i -> i == n || i % n != 0;
        primes = filter(sieve.apply(2), primes);
        for (int i = 3, n = (int) Math.sqrt(max); i <= n; i += 2)
            primes = filter(sieve.apply(i), primes);
        return primes;
    }

    @Test
    public void testPrime() {
        assertEquals(listOf(
            2, 3, 5, 7, 11, 13, 17, 19, 23,
            29, 31, 37, 41, 43, 47, 53, 59,
            61, 67, 71, 73, 79, 83, 89, 97), list(primes(100)));
    }

    static Iterable<Integer> primes2(int max) {
        Iterable<Integer> primes = range(2, max, 1);
        Function<Integer, Predicate<Integer>> sieve = n -> i -> i == n || i % n != 0;
        primes = filter(sieve.apply(2), primes);
        primes = filter(sieve.apply(3), primes);
        int n = (int) Math.sqrt(max);
        for (int i = 5; i <= n; i += 6) {
            primes = filter(sieve.apply(i), primes);
            primes = filter(sieve.apply(i + 2), primes);
        }
        return primes;
    }

    @Test
    public void testPrime2() {
        assertEquals(listOf(
            2, 3, 5, 7, 11, 13, 17, 19, 23,
            29, 31, 37, 41, 43, 47, 53, 59,
            61, 67, 71, 73, 79, 83, 89, 97), list(primes2(100)));
    }

    static int factorial(int n) {
        return reduce(1, (a, b) -> a * b, range(1, n + 1, 1));
    }

    @Test
    public void testFactorial() {
        assertEquals(1, factorial(0));
        assertEquals(1, factorial(1));
        assertEquals(2, factorial(2));
        assertEquals(6, factorial(3));
        assertEquals(24, factorial(4));
        assertEquals(120, factorial(5));
        assertEquals(720, factorial(6));
    }

    @Test
    public void testForEach() {
        List<Integer> result = new ArrayList<>();
        forEach(e -> result.add(e), listOf(0, 1, 2));
        assertEquals(listOf(0, 1, 2), result);
    }

    @Test
    public void testFindAny() {
        assertEquals(Integer.valueOf(5), findAny(x -> x.equals(5),
            listOf(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5)));
        assertNull(findAny(x -> x.equals(7),
            listOf(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5)));
    }

    @Test
    public void testFindFirst() {
        assertEquals(Integer.valueOf(0), findFirst(listOf(0, 1, 2)));
        assertNull(findFirst(listOf()));
    }
}
