package common.list_arrays;

import common.math.MathUtils;
import org.apache.commons.math3.util.Pair;
import java.util.*;
import java.util.stream.DoubleStream;

public class ArrayUtil {

    public static double sum(double[] array) {
        return Arrays.stream(array).sum();
    }

    public static double[] createArrayWithSameDoubleNumber(int length, double value) {
        double[] array = new double[length];
        Arrays.fill(array, value);
        return array;
    }

    public static double[] createArrayInRange(double start,double step, double end) {
        long count = (long) Math.ceil((end - start) / step) + 1;
        return DoubleStream.iterate(start, n -> n + step).limit(count).toArray();
    }

    public static double[] clip(double[] x, double min, double max) {
        for (int i = 0; i < x.length; i += 1) {
            x[i] = MathUtils.clip(x[i], min, max);
        }
        return x;
    }

    public static boolean isDoubleArraysEqual(double[] x, double[] y, double tol) {
        if (x.length != y.length) {
            return false;
        }
        for (int i = 0; i < x.length; i += 1) {
            if (Math.abs((y[i] - x[i])) > tol) {
                return false;
            }
        }
        return true;
    }

    public static int[][] transposeMatrix(int[][] matrix) {
        if (matrix.length == 0 || matrix[0].length == 0) {
            return new int[0][0];
        }
        int numRows = matrix.length; // Original number of rows
        int numCols = matrix[0].length; // Original number of columns
        int[][] transposedMatrix = new int[numCols][numRows];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                transposedMatrix[j][i] = matrix[i][j]; // Swap the row and column indices for the transposition
            }
        }
        return transposedMatrix;
    }


    public static Pair<Integer, Integer> getDimensions(int[][] data) {
        // Check for a null or empty array to avoid NullPointerException
        if (data == null || data.length == 0 || data[0].length == 0) {
            return new Pair<>(0, 0); // Assuming you want to return (0, 0) for an empty or null array
        }
        int numberOfRows = data.length;
        int numberOfColumns = data[0].length; // Assuming a rectangular matrix
        return new Pair<>(numberOfRows, numberOfColumns);
    }

    public static double[]  multWithValue(double[] x,  double multiplier) {
       return   DoubleStream.of(x)
                .map(v -> v * multiplier)
                .toArray();
    }

    public static Double findMin(Double[][] data) {
        return Arrays.stream(data)       // Stream<Double[]>
                .flatMap(Arrays::stream)  // Stream<Double>
                .filter(n -> !Objects.isNull(n))
                .min(Double::compareTo)   // Optional<Double>
                .orElseThrow(() -> new IllegalArgumentException("The array must not be null or empty"));
    }

    public static Double findMax(Double[][] data) {
        return Arrays.stream(data)       // Stream<Double[]>
                .flatMap(Arrays::stream)  // Stream<Double>
                .filter(n -> !Objects.isNull(n))
                .max(Double::compareTo)   // Optional<Double>
                .orElseThrow(() -> new IllegalArgumentException("The array must not be null or empty"));
    }

    public static Pair<double[], double[]> convertMapToArrays(Map<Double, Double> map) {
         List<Double> xList = new ArrayList<>(map.keySet());
        Collections.sort(xList);  // Sort the x values

        List<Double> yList = new ArrayList<>();
        for (Double x : xList) {
            yList.add(map.get(x));  // Arrange y values according to the sorted x values
        }

        double[] xArray = xList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] yArray = yList.stream().mapToDouble(Double::doubleValue).toArray();

        return Pair.create(xArray, yArray);
    }
}
