package common.plotters.parallel_coordinates;
import java.util.ArrayList;
import java.util.List;

public class NormalizeLineData {

    private NormalizeLineData() {
    }
    public static List<LineData> normalize(List<LineData> dataList) {
        int numInputs = dataList.get(0).valuesInput().size();

        // Find min and max for each input variable
        double[] minInputs = new double[numInputs];
        double[] maxInputs = new double[numInputs];
        initializeMinMaxArrays(minInputs, maxInputs);

        // Find min and max for valueOutput
        double minOutput = Double.MAX_VALUE;
        double maxOutput = Double.MIN_VALUE;

        // Calculate min and max values for each input and output
        for (LineData lineData : dataList) {
            for (int i = 0; i < numInputs; i++) {
                double value = lineData.valuesInput().get(i);
                minInputs[i] = Math.min(minInputs[i], value);
                maxInputs[i] = Math.max(maxInputs[i], value);
            }
            double outputValue = lineData.valueOutput();
            minOutput = Math.min(minOutput, outputValue);
            maxOutput = Math.max(maxOutput, outputValue);
        }

        // Normalize the data
        double finalMinOutput = minOutput;
        double finalMaxOutput = maxOutput;
        return dataList.stream()
                .map(lineData -> new LineData(
                        normalizeValues(lineData.valuesInput(), minInputs, maxInputs),
                        normalizeValue(lineData.valueOutput(), finalMinOutput, finalMaxOutput),
                        lineData.category()
                ))
                .toList();
    }

    private static void initializeMinMaxArrays(double[] minInputs, double[] maxInputs) {
        for (int i = 0; i < minInputs.length; i++) {
            minInputs[i] = Double.MAX_VALUE;
            maxInputs[i] = Double.MIN_VALUE;
        }
    }

    private static List<Double> normalizeValues(List<Double> values, double[] minInputs, double[] maxInputs) {
        List<Double> normalizedValues = new ArrayList<>(values.size());
        for (int i = 0; i < values.size(); i++) {
            normalizedValues.add(normalizeValue(values.get(i), minInputs[i], maxInputs[i]));
        }
        return normalizedValues;
    }

    private static double normalizeValue(double value, double min, double max) {
        return (value - min) / (max - min);
    }
}
