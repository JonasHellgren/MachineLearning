package common.math;

import org.apache.commons.math3.util.Pair;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MathUtils {

    private static final Logger logger = Logger.getLogger(MathUtils.class.getName());


    public static double clipBetwenZeroAndOne(double value) {
        return clip(value,0,1);
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

    public static boolean isNonZero(double value) {
        return !isZero(value);
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

    public static boolean isInRange(double value, double min, double max) {
        return (value>=min && value<=max);
    }

    public static boolean isInRange(int value, int min, int max) {
        return (value>=min && value<=max);
    }

    public static double setAsSmallIfZero(double value) {
        return (isZero(value)?Double.MIN_VALUE:value);
    }

    public static String getRoundedNumberAsString(Double value, int nofDigits)  {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);  //ENGLISH => point
        nf.setMaximumFractionDigits(nofDigits);
        return nf.format(value);
    }

    public static double normalize(double x,double xMin,double xMax) {
        return (x-xMin)/(xMax-xMin);
    }


    public static boolean compareIntScalars(int s1,int s2, int delta)
    {
        return (Math.abs(s1-s2)<delta);
    }

    public static boolean isEqualDoubles(double s1, double s2, double delta)
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

    public static List<Double> accumulatedSum(List<Double> list) {
        // Use AtomicReference to hold the running total
        AtomicReference<Double> runningSum = new AtomicReference<>(0.0);
        return list.stream().map(numberInList -> runningSum.updateAndGet(sum -> sum + numberInList))
                .collect(Collectors.toList());
    }

    public static  double pdf(double x, Pair<Double,Double> meanAndStd) {
        return pdf(x,meanAndStd.getFirst(),meanAndStd.getSecond());
    }

    public static  double pdf(double x, double m, double s) {
        return (1 / (s * Math.sqrt(2 * Math.PI))) *
                Math.exp(-0.5 * Math.pow((x - m) / s, 2));
    }


}
