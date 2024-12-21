package book_rl_explained.radial_basis;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import static book_rl_explained.radial_basis.RbfNetworkHelper.*;

@AllArgsConstructor
@Getter
public class RbfNetwork {

    Kernels kernels;
    Activations activations;
    Weights weights;
    WeightUpdater updater;

    public static RbfNetwork of(Kernels kernels, double learningRate) {
        return new RbfNetwork(kernels,
                Activations.empty(),
                Weights.allZero(kernels.size()),
                WeightUpdater.of(learningRate));
    }

    public int nKernels() {
        return kernels.size();
    }

     /**
     * Trains the RBF network for a specified number of iterations.
     *
     * @param data the training data
     * @param nFits the number of iterations to train for
     */
    public void train(TrainData data, int nFits) {
        IntStream.range(0, nFits).forEach(i -> fit(data));
    }

    /**
     * Trains the RBF network for a single iteration using the provided training data.
     *
     * @param data the training data
     */
    public void fit(TrainData data) {
        if (data.isErrors()) {
            fitFromErrors(data.inputs(), data.errors());
        } else {
            var errors = getErrors(data.inputs(), data.outputs());
            fitFromErrors(data.inputs(), errors);
        }
    }

    /**
     * Updates the weights of the RBF network based on the input data and target outputs.
     *
     * @param inputs   the input data
     * @param outputs the target outputs
     */

    public void fit(List<List<Double>> inputs, List<Double> outputs) {
        fitFromErrors(inputs, getErrors(inputs, outputs));
    }

    /**
     * Updates the weights of the RBF network based on the input data and error values.
     *
     * @param inputs  the input data
     * @param errors  the error values
     */
    public void fitFromErrors(List<List<Double>> inputs, List<Double> errors) {
        var data = TrainData.ofErrors(inputs, errors);
        activations=createActivationIfNeeded(data.nSamples(), activations);
        activations.calculateActivations(data, kernels);
        updater.updateWeights(data, weights, activations);
    }

    /**
     * Computes the output of the RBF network for a given input.
     *
     * @param input  the input data
     * @return the output of the RBF network
     */
    public double outPut(List<Double> input) {
        validateInput(input, weights, kernels);
        var activation = kernels.getActivationOfSingleInput(input);
        activations=createActivationIfNeeded(1, activations);
        activations.set(0, activation);
        return calcOutput(activation, weights);
    }


    /**
     * Handy to save computation time if you already have the activations in other identical rbf
     * @param other, the other rbf to copy from
     */

    public void copyActivations(RbfNetwork other) {
        validateOtherRbf(other, nKernels());
        Activations activationsOther = other.activations;
        activations = Activations.of(activationsOther.nSamples());
        RbfNetworkHelper.copyActivations(activations, activationsOther);
    }


    private List<Double> getErrors(List<List<Double>> inputs, List<Double> yTargets) {
        List<Double> errors = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            double error = yTargets.get(i) - outPut(inputs.get(i));
            errors.add(error);
        }
        return errors;
    }

    public void setWeights(double[] doubles) {
            weights.setWeights(doubles);
    }

    @Override
    public String toString() {
        return "RbfNetwork{" +
                "kernels=" + kernels +
                ", activations=" + activations +
                ", weights=" + weights +
                '}';
    }

}
