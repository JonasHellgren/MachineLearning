package book_rl_explained.radial_basis;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RbfNetworkHelper {

    public static double calcOutput(int nKernels, List<Double> activation, Weights weights) {
        double result = 0;
        for (int i = 0; i < nKernels; i++) {
            result += weights.get(i) * activation.get(i);
        }
        return result;
    }

    public static void copyActivations(Activations activations,Activations activationsOther) {
        for(int i = 0; i < activationsOther.nSamples(); i++) {
            activations.change(i, activationsOther.get(i));
        }
    }

    public static void validateInput(List<Double> input, Weights weights, Kernels kernels) {
        int nKernels = kernels.size();
        Preconditions.checkArgument(nKernels > 0, "kernels should not be empty");
        Preconditions.checkArgument(nKernels == weights.size(),
                "kernels size should be same as weights length");
        int lengthCenterCoord = kernels.get(0).centerCoordinates().length;
        Preconditions.checkArgument(lengthCenterCoord == input.size(),
                "input size should be same as n dimension in any kernel, input size: " + input.size() +
                        ", lengthCenterCoord = " + lengthCenterCoord);
    }

}
