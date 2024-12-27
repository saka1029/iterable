package saka1029.iterable;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class TestIterables {

    @Test
    public void testStream() {
        List<Integer> result = Iterables.stream(List.of(1, 2, 3)).toList();
        assertEquals(List.of(1, 2, 3), result);
    }

}
