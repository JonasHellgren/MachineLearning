package book_rl_explained.radial_basis;

import java.util.ArrayList;
import java.util.List;


public record TrainData(
        List<List<Double>> inputs,
        List<Double> errors,
        List<Double> outputs
) {


    public static TrainData emptyFourOutputs() {
        return new TrainData(new ArrayList<>(),  null, new ArrayList<>());
    }

    public static TrainData emptyFourErrors() {
        return new TrainData(new ArrayList<>(), new ArrayList<>(),null);
    }

    public void addInOutPair(List<Double> input,double output) {
        inputs.add(input);
        outputs.add(output);
    }


    public static TrainData ofErrors(List<List<Double>> inputs, List<Double> errors) {
        return new TrainData(inputs, errors, null);
    }

    public static TrainData ofOutputs(List<List<Double>> inputs, List<Double> outputs) {
        return new TrainData(inputs, null, outputs);
    }

    public int nExamples() {
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
}
