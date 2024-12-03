package super_vised.radial_basis;

import com.google.common.base.Preconditions;
import common.list_arrays.Array2ListConverter;
import common.list_arrays.List2ArrayConverter;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 * This class is responsible for updating the weights of a Radial Basis Function (RBF) network.
 * It provides methods for updating the weights based on the input data and target outputs.
 */

@AllArgsConstructor
public class WeightUpdater {

    public static final double LEARNING_RATE = 0.1;
    RadialBasis radialBasis;
    double learningRate;

    public static WeightUpdater of(RadialBasis radialBasis) {
        return new WeightUpdater(radialBasis, LEARNING_RATE);
    }

    /**
     * Updates the weights of the RBF network based on the input data and target outputs.
     *
     * @param inputs    the input data
     * @param yTargets  the target outputs
     */

    public void updateWeights(List<List<Double>> inputs, List<Double>  yTargets) {
        double[][] array = List2ArrayConverter.convertListWithListToDoubleMat(inputs);
        double[] vector = List2ArrayConverter.convertListToDoubleArr(yTargets);
        updateWeights(array,vector);
    }

    public void  updateWeights(double[][] inputs, double[] yTargets) {
        var weights=radialBasis.weights;
        double[] gradient = weightGradient(inputs, yTargets);
        for (int i = 0; i < weights.length; i++) {
            weights[i] += learningRate * gradient[i];
        }
        radialBasis.setWeights(weights);
    }

    private List<Double> weightGradient(List<List<Double>> inputs, List<Double>  yTargets) {
        double[][] array = List2ArrayConverter.convertListWithListToDoubleMat(inputs);
        double[] vector = List2ArrayConverter.convertListToDoubleArr(yTargets);
        return Array2ListConverter.convertDoubleArrToList(weightGradient(array,vector));
    }

    private double[] weightGradient(double[][] inputs, double[] yTargets) {
        int nExamples= inputs.length;
        Preconditions.checkArgument(nExamples == yTargets.length, "inputs and yTargets should be same length");
        int nKernels = radialBasis.nKernels();
        double[] gradient = new double[nKernels];
        for (int idxKernel = 0; idxKernel < nKernels; idxKernel++) {
            for (int idxExample = 0; idxExample < inputs.length; idxExample++) {
                double[] x = inputs[idxExample];
                double activation = radialBasis.activation(x, radialBasis.getKernel(idxKernel));
                double yTarget = yTargets[idxExample];
                double yPredicted=radialBasis.outPut(x);
                gradient[idxKernel] += (yTarget-yPredicted) * activation;
            }
            gradient[idxKernel]=gradient[idxKernel]/nExamples;
        }
        return gradient;
    }

    public static double[] convertListToDoubleArr(List<Double> inList) {
        double[] outArray = new double[inList.size()];
        for (int i = 0; i < inList.size(); i++) {
            outArray[i] = inList.get(i); // Auto-unboxing converts Double to double
        }
        return outArray;
    }

}
