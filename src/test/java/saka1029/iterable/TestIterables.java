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
}
