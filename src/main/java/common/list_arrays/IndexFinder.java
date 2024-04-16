package common.list_arrays;

import lombok.extern.java.Log;

import java.util.Arrays;

@Log
public class IndexFinder {

    public static int findIndex(int[] arr, int t)
    {
        int index = Arrays.binarySearch(arr, t);

        System.out.println("index = " + index);
        return (index < 0) ? -1 : index;
    }

    public static int findBucket(double[] array, double value) {

        if (value <array[0]) {
            log.warning("value below first bucket");
            return -1;
        }

        for (int i = 0; i < array.length-1; i++) {
            if (value < array[i+1]) {
                return i;  // Value falls into bucket i
            }
        }

        log.warning("Value exceeds the last bucket");
        return array.length-1;
    }

}
