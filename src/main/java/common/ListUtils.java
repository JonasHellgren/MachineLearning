package common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ListUtils {

    public static OptionalDouble findMinInList(List<Double> list)  {
        return  list.stream().mapToDouble(Double::doubleValue)
                .min();
    }

    public static OptionalDouble findMaxInList(List<Double> list)  {
        return  list.stream().mapToDouble(Double::doubleValue)
                .max();
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

    public static List<Double> listWithZeroElements(int len) {
        return new ArrayList<>(Collections.nCopies(len,0d));
    }

    public static List<Double> listWithEqualElementValues(int len, double value) {
        return new ArrayList<>(Collections.nCopies(len,value));
    }

}
