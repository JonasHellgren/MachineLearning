package book_rl_explained.radial_basis;

import org.hellgren.utilities.random.RandUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of training data for a radial basis function.
 * Each example in the training data consists of an input, an error, and an output.
 */
public record TrainData(
        List<List<Double>> inputs,
        List<Double> errors,
        List<Double> outputs
) {


    public static TrainData emptyFourOutputs() {
        return new TrainData(new ArrayList<>(), null, new ArrayList<>());
    }

    public static TrainData emptyFourErrors() {
        return new TrainData(new ArrayList<>(), new ArrayList<>(), null);
    }

    public static TrainData ofErrors(List<List<Double>> inputs, List<Double> errors) {
        return new TrainData(inputs, errors, null);
    }

    public static TrainData ofOutputs(List<List<Double>> inputs, List<Double> outputs) {
        return new TrainData(inputs, null, outputs);
    }

    public void addInOutPair(List<Double> input, double output) {
        inputs.add(input);
        outputs.add(output);
    }

    public TrainData createBatch(int len) {
        var randomIndices = RandUtils.randomIndices(len, nSamples());
        var newInputs = new ArrayList<List<Double>>();
        if (isErrors()) {
            var newErrors = new ArrayList<Double>();
            for (int i : randomIndices) {
                newInputs.add(inputs.get(i));
                newErrors.add(errors.get(i));
            }
            return TrainData.ofErrors(newInputs, newErrors);
        } else {
            var newOutputs = new ArrayList<Double>();
            for (int i : randomIndices) {
                newInputs.add(inputs.get(i));
                newOutputs.add(outputs.get(i));
            }
            return TrainData.ofOutputs(newInputs, newOutputs);
        }
    }

    public int nSamples() {
        return inputs.size();
    }

    public boolean isErrors() {
        return errors != null;
    }

    public List<Double> input(int i) {
        return inputs.get(i);
    }

    public double output(int i) {
        return outputs.get(i);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (int i = 0; i < nSamples(); i++) {
            if (isErrors()) {
                sb.append(input(i)).append(" ").append(errors.get(i)).append("\n");
            } else {
                sb.append(input(i)).append(" ").append(output(i)).append("\n");
            }
        }
        return sb.toString();


    }

}
