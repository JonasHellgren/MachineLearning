package common;

import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Logger;

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

    public static final double[] interpLinear(double[] x, double[] y, double[] xi) throws IllegalArgumentException {

        if (x.length != y.length) {
            throw new IllegalArgumentException("X and Y must be the same length");
        }
        if (x.length == 1) {
            throw new IllegalArgumentException("X must contain more than one value");
        }
        double[] dx = new double[x.length - 1];
        double[] dy = new double[x.length - 1];
        double[] slope = new double[x.length - 1];
        double[] intercept = new double[x.length - 1];

        // Calculate the line equation (i.e. slope and intercept) between each point
        for (int i = 0; i < x.length - 1; i++) {
            dx[i] = x[i + 1] - x[i];
            if (dx[i] == 0) {
                throw new IllegalArgumentException("X must be monotonic. A duplicate " + "x-value was found");
            }
            if (dx[i] < 0) {
                throw new IllegalArgumentException("X must be sorted");
            }
            dy[i] = y[i + 1] - y[i];
            slope[i] = dy[i] / dx[i];
            intercept[i] = y[i] - x[i] * slope[i];
        }

        // Perform the interpolation here
        double[] yi = new double[xi.length];
        for (int i = 0; i < xi.length; i++) {
            if ((xi[i] > x[x.length - 1])) {
                yi[i] = y[y.length - 1];
                logger.warning("Interpolating outside range (higher)");
            } else if ((xi[i] < x[0])) {
                yi[i] = y[0];
                logger.warning("Interpolating outside range (lower)");
            }
            else {
                int loc = Arrays.binarySearch(x, xi[i]);
                if (loc < -1) {
                    loc = -loc - 2;
                    yi[i] = slope[loc] * xi[i] + intercept[loc];
                }
                else {
                    yi[i] = y[loc];
                }
            }
        }

        return yi;
    }


}
