package saka1029.iterable;

import static saka1029.iterable.Iterables.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.Test;

public class TestIterables {

    @Test
    public void testList() {
        assertEquals(List.of(), listOf());
        assertEquals(List.of(0, 1, 2), listOf(0, 1, 2));
        assertEquals(List.of("a", "b"), listOf("a", "b"));
    }

    @Test
    public void testArrayList() {
        ArrayList<Integer> list = arrayListOf(0, 1, 2);
        assertEquals(List.of(0, 1, 2), list);
    }

    @Test
    public void testLinkedList() {
        LinkedList<Integer> list = linkedListOf(0, 1, 2);
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

    @Test(expected = IllegalArgumentException.class)
    public void testRangeIllegalEnd() {
        list(range(0, 3, -1));
    }

    @Test
    public void testLinkedListFromIterable() {
        LinkedList<Integer> list = linkedList(listOf(0, 1, 2));
        assertEquals(List.of(0, 1, 2), list);
    }

    @Test
    public void testArray() {
        assertArrayEquals(new String[] {"a", "b", "c"},
            array(String[]::new, listOf("a", "b", "c")));
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
    public void testFilter() {
        assertEquals(listOf(1, 3),
            list(
                filter(x -> x % 2 != 0,
                    listOf(0, 1, 2, 3))));
    }

    @Test
    public void testStream() {
        assertEquals(listOf(1, 2, 3), stream(listOf(1, 2, 3)).toList());
    }

    @Test
    public void testHashMap() {
        HashMap<String, Integer> map = hashMap(s -> s, s -> s.length(), listOf("one", "two", "three"));
        assertEquals(Map.of("one", 3, "two", 3, "three", 5), map);
    }

    @Test
    public void testTreeMap() {
        TreeMap<String, Integer> map = treeMap(s -> s, s -> s.length(), listOf("one", "two", "three"));
        assertEquals(Map.of("one", 3, "two", 3, "three", 5), map);
    }

    @Test
    public void testMakeMap() {
        assertEquals(Map.of(0, "zero", 1, "one"),
            hashMap(listOf(0, 1), List.of("zero", "one")));
        assertEquals(Map.of(0, "zero", 1, "one"),
            hashMap(listOf(0, 1, 2), List.of("zero", "one")));
        assertEquals(Map.of(0, "zero", 1, "one"),
            hashMap(listOf(0, 1), List.of("zero", "one", "two")));
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
}
