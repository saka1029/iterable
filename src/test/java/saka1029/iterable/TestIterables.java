package saka1029.iterable;

import static saka1029.iterable.Iterables.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

}
