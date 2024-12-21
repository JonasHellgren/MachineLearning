package book_rl_explained.radialbasis;

import com.google.common.base.Preconditions;
import common.list_arrays.List2ArrayConverter;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * This class is responsible for updating the weights of a Radial Basis Function (RBF) network.
 * It provides methods for updating the weights based on the input data and target outputs.
 */

@AllArgsConstructor
public class WeightUpdaterOld {

    public static final double LEARNING_RATE = 0.1;
    RadialBasis radialBasis;
    double learningRate;

    public static WeightUpdaterOld of(RadialBasis radialBasis) {
        return new WeightUpdaterOld(radialBasis, LEARNING_RATE);
    }

    public static WeightUpdaterOld withLearningRate(RadialBasis radialBasis, double learningRate) {
        return new WeightUpdaterOld(radialBasis, learningRate);
    }



    /**
     * Updates the weights of the RBF network based on the input data and target outputs.
     *
     * @param inputs   the input data
     * @param yTargets the target outputs
     */

    public void updateWeights(List<List<Double>> inputs, List<Double> yTargets) {
        double[][] array = List2ArrayConverter.convertListWithListToDoubleMat(inputs);
        double[] vector = List2ArrayConverter.convertListToDoubleArr(yTargets);
        updateWeights(array, vector);
    }

    public void updateWeightsFromErrors(List<List<Double>> inputs, List<Double> yErrors) {
        double[][] array = List2ArrayConverter.convertListWithListToDoubleMat(inputs);
        double[] vector = List2ArrayConverter.convertListToDoubleArr(yErrors);
        updateWeightsFromErrors(array, vector);
    }

    public void updateWeights(double[][] inputs, double[] yTargets) {
        double[] yErrors = getErrors(inputs, yTargets);
        updateWeightsFromErrors(inputs, yErrors);
    }

    /**
     * Updates the weights of the RBF network based on the input data and errors.
     *
     * @param inputs  the input data
     * @param yErrors the target errors
     */

    public void updateWeightsFromErrors(double[][] inputs, double[] yErrors) {
        double[] gradient = weightGradientFromErrors(inputs, yErrors);
        var weights = radialBasis.weights;
        for (int i = 0; i < weights.length; i++) {
            weights[i] += learningRate * gradient[i];
        }
        radialBasis.setWeights(weights);
    }

    /***
     * gradient[idxKernel] =
     * (1/nExamples) * ∑[yTarget[i] - yPredicted[i]] * φ(x[i], idxKernel)
     */

    private double[] weightGradient(double[][] inputs, double[] yTargets) {
        double[] yErrors = getErrors(inputs, yTargets);
        return weightGradientFromErrors(inputs, yErrors);
    }

    /***
     * The function computes the gradient of weights for a radial basis function by aggregating the weighted errors
     * (error × activation) for all input examples for each kernel.
     *
     * gradient[idxKernel] = (1/nExamples) * ∑yErr[i] * φ(x[i], idxKernel)
     */

    private double[] weightGradientFromErrors(double[][] inputs, double[] yErrors) {
        int nExamples = inputs.length;
        Preconditions.checkArgument(nExamples == yErrors.length, "inputs and yTargets should be same length");
        int nKernels = radialBasis.nKernels();
        double[] gradient = new double[nKernels];
        for (int idxKernel = 0; idxKernel < nKernels; idxKernel++) {
            for (int idxExample = 0; idxExample < inputs.length; idxExample++) {
                double[] x = inputs[idxExample];
                double activation = radialBasis.activation(x, radialBasis.getKernel(idxKernel));
                double yErr = yErrors[idxExample];
                gradient[idxKernel] += yErr * activation;
            }
            gradient[idxKernel] = gradient[idxKernel] / nExamples;
        }
        return gradient;
    }


    private double[] getErrors(double[][] inputs, double[] yTargets) {
        double[] yErrors = new double[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            yErrors[i] = yTargets[i] - radialBasis.outPut(inputs[i]);
        }
        return yErrors;
    }


}
