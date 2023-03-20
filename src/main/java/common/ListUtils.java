package common;


import java.lang.reflect.Field;
import java.util.*;
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

    public static OptionalDouble findAverage(List<Double> list) {
        return list.stream()
                .mapToDouble(a -> a)
                .average();
    }


    public static <T> Optional<T> findEnd(List<T> list) {
        if (list.size()==0) {
            return Optional.empty();
        }
        return Optional.of(list.get(list.size()-1));
    }

//todo fix warning
    public static <T,V> List<V> getListOfField(List<T> list, String fieldName) throws NoSuchFieldException {

        if (list.size()==0) {
            return new ArrayList<>();
        }

        Field field = list.get(0).getClass().getDeclaredField(fieldName);
        return list.stream().map(e -> {
            try {
                return (V) field.get(e);
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
            return null;

        }).collect(Collectors.toList());
    }

    public static List<Double> sumListElements(List<Double> listA, List<Double> listB) {
        return IntStream.range(0, listA.size())
                .mapToObj(i -> listA.get(i) + listB.get(i))
                .collect(Collectors.toList());
    }



    public static Integer sumIntegerList(List<Integer> list) {
        return list.stream().mapToInt(Integer::intValue).sum();
    }


    public static Double sumDoubleList(List<Double> list) {
        return list.stream().mapToDouble(Double::doubleValue).sum();
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

    public static double dotProduct(List<Double> listA,List<Double> listB)  {
        return IntStream.range(0, Math.min(listA.size(), listB.size()))
                .mapToDouble(i -> listA.get(i) * listB.get(i))
                .sum();
    }

    public static List<Double> elementProduct(List<Double> listA,List<Double> listB)  {
        return IntStream.range(0, Math.min(listA.size(), listB.size()))
                .mapToObj(i -> listA.get(i) * listB.get(i))
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

    /**
     *  list = [10 10 10], df=0.5 => listDf=[1*df^0 1*df^1 1*df^2] => dotProduct(list,listDf)=10+5+2.5
     */
    public static double discountedSum(List<Double> list, double discountFactor) {
        List<Double> listDf = getDiscountList(list.size(), discountFactor);
        return dotProduct(list,listDf);
    }

    public static List<Double> discountedElements(List<Double> list, double discountFactor) {
        List<Double> listDf = getDiscountList(list.size(), discountFactor);
        return elementProduct(list,listDf);
    }


    public static List<Double> discountedElementsReverse(List<Double> list, double discountFactor) {
        List<Double> listDf = getDiscountList(list.size(), discountFactor);
        Collections.reverse(listDf);
        return elementProduct(list,listDf);
    }

    /*
    rewards=[0,1,1] => returns=[2,2,1]
    */
    public static List<Double> getReturns(List<Double> rewards) {
        double singleReturn = 0;
        List<Double> returns = new ArrayList<>();
        for (int i = rewards.size() - 1; i >= 0; i--) {
            singleReturn = singleReturn + rewards.get(i);
            returns.add(singleReturn);
        }
        Collections.reverse(returns);
        return returns;
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

    public static List<Double> getDiscountList(int len, double discountFactor) {
        List<Double> listDf = new ArrayList<>();
        double df = 1;
        for (int i = 0; i < len; i++) {
            listDf.add(df);
            df = df * discountFactor;
        }
        return listDf;
    }

    public static<T> List<T> merge(List<T> list1, List<T> list2)
    {
        List<T> list = new ArrayList<>(list1);
        list.addAll(list2);

        return list;
    }

}
