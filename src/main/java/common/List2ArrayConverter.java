package common;

import java.util.List;

public class List2ArrayConverter {

    private List2ArrayConverter() {
    }

    public static double[] convertListToDoubleArr(List<Double> inList) {
        // Initialize the array with the size of the input list
        double[] outArray = new double[inList.size()];

        // Iterate over the list to populate the array
        for (int i = 0; i < inList.size(); i++) {
            outArray[i] = inList.get(i); // Auto-unboxing converts Double to double
        }

        return outArray;
    }

        public static double[][] convertListWithListToDoubleMat(List<List<Double>> inList) {
        // Check if the input list is not empty to avoid IndexOutOfBoundsException
        if (inList.isEmpty()) {
            return new double[0][0]; // Return an empty 2D array if the input list is empty
        }

        // Initialize the 2D array with the size of the outer list as the first dimension
        double[][] outArray = new double[inList.size()][];

        // Iterate over the outer list to populate the 2D array
        for (int i = 0; i < inList.size(); i++) {
            // Initialize the inner array with the size of the inner list
            List<Double> innerList = inList.get(i);
            outArray[i] = new double[innerList.size()];

            // Convert each Double object to double primitive type and store it in the inner array
            for (int j = 0; j < innerList.size(); j++) {
                outArray[i][j] = innerList.get(j); // Auto-unboxing converts Double to double
            }
        }

        return outArray;
    }
}
