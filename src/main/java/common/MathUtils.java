package common;

import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MathUtils {

    private static final Logger logger = Logger.getLogger(MathUtils.class.getName());


    public static double calcRandomFromInterval(double minValue, double maxValue) {
        return minValue+Math.random()*(maxValue-minValue);
    }

    public static double clip(double variable, double minValue, double maxValue) {
        double lowerThanMax= Math.min(variable, maxValue);
        return Math.max(lowerThanMax, minValue);
    }

    public static int clip(int variable, int minValue, int maxValue) {
        int lowerThanMax= Math.min(variable, maxValue);
        return Math.max(lowerThanMax, minValue);
    }

    public static boolean isZero(double value) {
        return (Math.abs(value-0)<2*Double.MIN_VALUE);
    }

    public static boolean isZero(int value) {
        return (value==0);
    }

    public static boolean isNeg(double value) {
        return value<-Double.MIN_VALUE;
    }

    public static boolean isPos(double value) {
        return value>Double.MIN_VALUE;
    }

    public static double setAsSmallIfZero(double value) {
        return (isZero(value)?Double.MIN_VALUE:value);
    }

    public static String getRoundedNumberAsString(Double value, int nofDigits)  {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);  //ENGLISH => point
        nf.setMaximumFractionDigits(nofDigits);
        return nf.format(value);
    }

    public static OptionalDouble findMinInList(List<Double> list)  {
        return  list.stream().mapToDouble(Double::doubleValue)
                .min();
    }

    public static OptionalDouble findMaxInList(List<Double> list)  {
        return  list.stream().mapToDouble(Double::doubleValue)
                .max();
    }

    public static double normalize(double x,double xMin,double xMax) {
        return (x-xMin)/(xMax-xMin);
    }

    public static String getRandomItemFromStringList(List<String> list) {
        Random random=new Random();  //todo, not new
        return list.get(random.nextInt(list.size()));
    }

    public static Integer getRandomItemFromIntegerList(List<Integer> list) {
        Random random=new Random();
        return list.get(random.nextInt(list.size()));
    }


    public static boolean compareIntScalars(int s1,int s2, int delta)
    {
        return (Math.abs(s1-s2)<delta);
    }

    public static boolean compareDoubleScalars(double s1,double s2, double delta)
    {
        return (Math.abs(s1-s2)<delta);
    }

    public static boolean compareDoubleArrays(double arr1[],double arr2[], double delta)
    {
        if (arr1.length != arr2.length)
            return false;

        for (int i = 0; i < arr1.length; i++) {

            if (Math.abs(arr1[i] - arr2[i])>delta)
                return false;
        }
        return true;
    }

    public static List<Double> sumListElements(List<Double> listA, List<Double> listB) {
        return IntStream.range(0, listA.size())
                .mapToObj(i -> listA.get(i) + listB.get(i))
                .collect(Collectors.toList());
    }

}
