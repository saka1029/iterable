package saka1029.iterable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class TestIterables {

    @Test
    public void testIterable() {
        Iterable<Integer> iterable = Iterables.iterable(() -> new Iterator<Integer>() {
            int i = 0, max = 3;

            @Override
            public boolean hasNext() {
                return i < max;
            }

            @Override
            public Integer next() {
                return i++;
            }
        });
        Iterator<Integer> iterator = iterable.iterator();
        assertEquals(0, (int)iterator.next());
        assertEquals(1, (int)iterator.next());
        assertEquals(2, (int)iterator.next());
        assertFalse(iterator.hasNext());
        iterator = iterable.iterator();
        assertEquals(0, (int)iterator.next());
        assertEquals(1, (int)iterator.next());
        assertEquals(2, (int)iterator.next());
        assertFalse(iterator.hasNext());

    }

    @Test
    public void testStream() {
        List<Integer> result = Iterables.stream(List.of(1, 2, 3)).toList();
        assertEquals(List.of(1, 2, 3), result);
    }

}
