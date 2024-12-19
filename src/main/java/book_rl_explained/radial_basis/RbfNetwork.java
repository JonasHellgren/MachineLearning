package book_rl_explained.radial_basis;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import static book_rl_explained.radial_basis.RbfNetworkHelper.calcOutput;
import static book_rl_explained.radial_basis.RbfNetworkHelper.validateInput;
import static org.hellgren.utilities.conditionals.Conditionals.executeIfTrue;

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

    public Kernel getKernel(int i) {
        return kernels.get(i);
    }

    /**
     * Trains the RBF network for a specified number of iterations.
     *
     * @param data the training data
     * @param nFits the number of iterations to train for
     */
    public void train(TrainData data, int nFits) {
        IntStream.range(0, nFits).forEach(i -> train(data));
    }

    /**
     * Trains the RBF network for a single iteration using the provided training data.
     *
     * @param data the training data
     */
    public void train(TrainData data) {
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

    public void train(List<List<Double>> inputs, List<Double> outputs) {
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
        int nSamples=data.nSamples();
        executeIfTrue(activations.nSamples()!=nSamples, () ->
                activations = Activations.of(nSamples));
        activations.calcuteActivations(data, kernels);
        updater.updateWeightsFromErrors(data, weights, activations);
    }

    /**
     * Computes the output of the RBF network for a given input.
     *
     * @param input  the input data
     * @return the output of the RBF network
     */
    public double outPut(List<Double> input) {
        int nKernels = kernels.size();
        validateInput(input, weights, kernels);
        var activation = kernels.getActivationOfSingleInput(input);
        executeIfTrue(activations.nSamples()!=1, () ->
                activations = Activations.of(1));
        activations.change(0, activation);
        return calcOutput(nKernels, activation, weights);
    }

    /**
     * Handy to save computation time if you already have the activations in other identical rbf
     * @param other, the other rbf to copy from
     */

    public void copyActivations(RbfNetwork other) {
        Preconditions.checkArgument(other.nKernels() == nKernels()
                ,"kernels should be same size");
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


}
