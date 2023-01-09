package common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class ListUtils {

    public static OptionalDouble findMin(List<Double> list)  {
        return  list.stream().mapToDouble(Double::doubleValue)
                .min();
    }

    public static OptionalDouble findMax(List<Double> list)  {
        return  list.stream().mapToDouble(Double::doubleValue)
                .max();
    }

    public OptionalDouble findAverage(List<Double> list) {
        return list.stream()
                .mapToDouble(a -> a)
                .average();
    }

    public static List<Double> sumListElements(List<Double> listA, List<Double> listB) {
        return IntStream.range(0, listA.size())
                .mapToObj(i -> listA.get(i) + listB.get(i))
                .collect(Collectors.toList());
    }

    public static List<Double> addScalarToListElements(List<Double> listA, Double scalar) {
        return listA.stream().map(num -> num + scalar)
                .collect(Collectors.toList());
    }

    public static List<Double> multiplyListElements(List<Double> list, double scalar) {
        return list.stream()
                .map(num -> num * scalar)
                .collect(Collectors.toList());
    }

    public static List<Double> createListWithZeroElements(int len) {
        return createListWithEqualElementValues(len,0);
    }

    public static List<Double> createListWithEqualElementValues(int len, double value) {
        return new ArrayList<>(Collections.nCopies(len,value));
    }

    public static List<Double> generateSequenceDoubleStream(double start, double end, double step) {
        return DoubleStream.iterate(start, d -> d <= end, d -> d + step)
                .boxed()
                .collect(Collectors.toList());
    }

    public static double[] toArray(List<Double> list) {
        return list.stream().mapToDouble(Number::doubleValue).toArray();
    }

    public static boolean isDoubleArraysEqual(double[] x, double[] y, double tol)
    {
        if (x.length!=y.length) {
            return false;
        }
        for (int i = 0; i < x.length; i += 1)
        {
            if (Math.abs((y[i] - x[i])) > tol)
            {
                return false;
            }
        }
        return true;
    }

}
