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
        assertEquals(List.of(), list());
        assertEquals(List.of(0, 1, 2), list(0, 1, 2));
        assertEquals(List.of("a", "b"), list("a", "b"));
    }

    @Test
    public void testArrayList() {
        ArrayList<Integer> list = arrayList(0, 1, 2);
        assertEquals(List.of(0, 1, 2), list);
    }

    @Test
    public void testLinkedList() {
        LinkedList<Integer> list = linkedList(0, 1, 2);
        assertEquals(List.of(0, 1, 2), list);
    }

    @Test
    public void testLinkedListFromIterable() {
        LinkedList<Integer> list = linkedList(list(0, 1, 2));
        assertEquals(List.of(0, 1, 2), list);
    }

    @Test
    public void testArray() {
        assertArrayEquals(new String[] {"a", "b", "c"}, array(String[]::new, list("a", "b", "c")));
    }

    @Test
    public void testStream() {
        List<Integer> result = Iterables.stream(List.of(1, 2, 3)).toList();
        assertEquals(List.of(1, 2, 3), result);
    }

}
