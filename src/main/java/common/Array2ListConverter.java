package common;

import java.util.ArrayList;
import java.util.List;

public class Array2ListConverter {


    public static List<Double> convertDoubleArrToList(double[] inVec) {
        // Initialize the list with the capacity equal to the length of inVec for efficiency
        List<Double> outList = new ArrayList<>(inVec.length);

        // Iterate over the array and add each double value to the list
        for (double val : inVec) {
            outList.add(val); // Auto-boxing converts double to Double
        }

        return outList;
    }

    public static List<List<Double>> convertDoubleMatToListOfLists(double[][] inMat) {
        // Initialize the outer list
        List<List<Double>> outList = new ArrayList<>();

        // Iterate over the first dimension of the 2D array
        for (double[] row : inMat) {
            // Initialize the inner list for the current row
            List<Double> innerList = new ArrayList<>();

            // Iterate over each element in the row
            for (double val : row) {
                // Wrap the primitive double to Double and add it to the inner list
                innerList.add(val);
            }

            // Add the populated inner list to the outer list
            outList.add(innerList);
        }

        return outList;
    }


}
