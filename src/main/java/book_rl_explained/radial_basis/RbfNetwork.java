package book_rl_explained.radial_basis;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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


    public void train(TrainData data, int nFits) {
        for (int i = 0; i < nFits; i++) {
            train(data);
        }
    }

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
     * @param yTargets the target outputs
     */

    public void train(List<List<Double>> inputs, List<Double> yTargets) {
        var errors = getErrors(inputs, yTargets);
        fitFromErrors(inputs, errors);
    }

    public void fitFromErrors(List<List<Double>> inputs, List<Double> errors) {
        var data = TrainData.ofErrors(inputs, errors);
        activations = Activations.of(data.inputs().size());
        activations.setActivations(data, kernels);
        updater.updateWeightsFromErrors(data, weights, activations);
    }

    public double outPut(List<Double> input) {
        int nKernels = kernels.size();
        validateInput(input, nKernels);
        var activation = kernels.getActivationOfSingleInput(input);
        double result = 0;
        for (int i = 0; i < nKernels; i++) {
            result += weights.get(i) * activation.get(i);
        }
        return result;
    }

    private void validateInput(List<Double> input, int nKernels) {
        Preconditions.checkArgument(nKernels > 0, "kernels should not be empty");
        Preconditions.checkArgument(nKernels == weights.size(),
                "kernels size should be same as weights length");
        int lengthCenterCoord = kernels.get(0).centerCoordinates().length;
        Preconditions.checkArgument(lengthCenterCoord == input.size(),
                "input size should be same as n dimension in any kernel, input size: " + input.size() +
                        ", lengthCenterCoord = " + lengthCenterCoord);
    }

    public void copyActivations(RbfNetwork other) {

    }


    private List<Double> getErrors(List<List<Double>> inputs, List<Double> yTargets) {
        //double[] yErrors = new double[inputs.length];
        List<Double> errors = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            //yErrors[i] = yTargets[i] - outPut(inputs[i]);
            double error = yTargets.get(i) - outPut(inputs.get(i));
            errors.add(error);
        }
        return errors;
    }


}
