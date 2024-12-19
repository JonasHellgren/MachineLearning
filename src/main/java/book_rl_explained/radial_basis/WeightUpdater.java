package book_rl_explained.radial_basis;

import com.google.common.base.Preconditions;
import common.list_arrays.List2ArrayConverter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WeightUpdater {

    double learningRate;


    public static WeightUpdater of(double learningRate) {
        return new WeightUpdater(learningRate);
    }


    public void updateWeights(TrainData data, Weights weights, Activations activations) {
        Preconditions.checkArgument(weights.size() == activations.nKernels(),
                "weights and nKernels should have same length, nWeights = " + weights.size()
                        + ", nKernels = " + activations.nKernels());

        updateWeightsFromErrors(data, weights, activations);
    }


    /**
     * Updates the weights based on the errors and activations.
     *
     * @param data The training data.
     * @param weights The current weights.
     * @param activations The activations of the network.
     */

    public void updateWeightsFromErrors(TrainData data, Weights weights, Activations activations) {
        double[] gradient = weightGradientFromErrors(data, activations);
        for (int i = 0; i < weights.size(); i++) {
            //weights[i] += learningRate * gradient[i];
            double wOld= weights.get(i);
            weights.set(i,wOld+ learningRate * gradient[i]);
        }
    }


    /***
     * The function computes the gradient of weights for a radial basis function by aggregating the weighted errors
     * (error × activation) for all input examples for each kernel.
     *
     * gradient[idxKernel] = (1/nExamples) * ∑yErr[i] * φ(x[i], idxKernel)
     */

    private double[] weightGradientFromErrors(TrainData data, Activations activations) {

        double[][] in = List2ArrayConverter.convertListWithListToDoubleMat(data.inputs());
        double[] err = List2ArrayConverter.convertListToDoubleArr(data.errors());
        int nExamples = in.length;
        Preconditions.checkArgument(nExamples == err.length, "inputs and err should be same length");
        int nKernels = activations.nKernels();
        double[] gradient = new double[nKernels];
        for (int idxKernel = 0; idxKernel < nKernels; idxKernel++) {
            for (int idxExample = 0; idxExample < nExamples; idxExample++) {
                //double[] x = in[idxExample];
                //double activation = radialBasis.activation(x, radialBasis.getKernel(idxKernel));
                double activation = activations.get(idxExample,idxKernel);
                double error = err[idxExample];
                gradient[idxKernel] += error * activation;
            }
            gradient[idxKernel] = gradient[idxKernel] / nExamples;
        }
        return gradient;
    }


}
