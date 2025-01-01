package saka1029.iterable;

import static saka1029.iterable.Iterables.*;
import java.util.Arrays;
import org.junit.Test;

public class TestPermutationNext {

    static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    static void reverse(int[] array, int start, int end) {
        while (start < end)
            swap(array, start++, end--);
    }

    static boolean next(int[] array, int k) {
        int length = array.length;
        int i = length - 2;
        while (i >= 0 && array[i] >= array[i + 1])
            --i;
        if (i < 0)
            return false;
        int j = length - 1;
        while (array[i] >= array[j])
            --j;
        swap(array, i, j);
        reverse(array, i + 1, length - 1);
        return true;
    }

    @Test
    public void testNext() {
        int[] perm = intArray(range(0, 4, 1));
        do {
            System.out.println(Arrays.toString(perm));
        } while (next(perm, 3));
    }
}
