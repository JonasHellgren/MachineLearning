package book_rl_explained.radial_basis;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    public void change(int idxExample, List<Double> activations) {
      activationsAllExamples.set(idxExample, activations);
    }

    public double get(int idxExample,int idxKernel) {
        return activationsAllExamples.get(idxExample).get(idxKernel);
    }

    public  int nExamples() {
        return activationsAllExamples.size();
    }

    public int nKernels() {
        Preconditions.checkArgument(nExamples() > 0, "activations should not be empty");
        return activationsAllExamples.get(0).size();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (List<Double> activation : activationsAllExamples) {
            int idx = activationsAllExamples.indexOf(activation);
            sb.append("  Example idx:="+ idx+":").append(activation).append("\n");
    }
        return "\n"+sb.toString();
    }


}
