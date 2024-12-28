package saka1029.iterable;

import static saka1029.iterable.Iterables.*;
import static org.junit.Assert.assertEquals;

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
    public void testStream() {
        List<Integer> result = Iterables.stream(List.of(1, 2, 3)).toList();
        assertEquals(List.of(1, 2, 3), result);
    }

}
