package common;

import java.util.Arrays;

 public  class ArrayUtil {


    public static double sum(double[] array) {
        return Arrays.stream(array).sum();
    }


}
