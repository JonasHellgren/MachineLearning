package book_rl_explained.radial_basis;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the activations of a set of examples.
 * Each example has a list of activations corresponding to different kernels.
 */
@AllArgsConstructor
public class Activations {

    List<List<Double>> activationsAllExamples;  //  nExamples x nKernels

    public static Activations of(int nExamples) {
        List<List<Double>> list = new ArrayList<>();
        for (int i = 0; i < nExamples; i++) {
            list.add(new ArrayList<>());
        }
        return new Activations(list);
    }

    public static Activations empty() {
        return new Activations(new ArrayList<>());
    }

    public void setActivations(TrainData data, Kernels kernels) {
        for (int i = 0; i < data.inputs().size(); i++) {
            var input = data.inputs().get(i);
            var activationsOfInput = kernels.getActivationOfSingleInput(input);
            change(i, activationsOfInput);
        }
    }

    /**
     * Changes the activations for a specific example.
     *
     * @param idxSample the index of the example
     * @param activations the new activations
     */

    public void change(int idxSample, List<Double> activations) {
        activationsAllExamples.set(idxSample, activations);
    }

    /**
     * Gets the activation for a specific example and kernel.
     *
     * @param idxSample the index of the sample
     * @param idxKernel the index of the kernel
     * @return the activation
     */
    public double get(int idxSample, int idxKernel) {
        return get(idxSample).get(idxKernel);
    }

    public List<Double> get(int idxSample) {
        return activationsAllExamples.get(idxSample);
    }

    public int nSamples() {
        return activationsAllExamples.size();
    }

    public int nKernels() {
        Preconditions.checkArgument(nSamples() > 0, "activations should not be empty");
        return activationsAllExamples.get(0).size();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (List<Double> activation : activationsAllExamples) {
            int idx = activationsAllExamples.indexOf(activation);
            sb.append("  Sample idx:=" + idx + ":").append(activation).append("\n");
        }
        return "\n" + sb.toString();
    }


}
