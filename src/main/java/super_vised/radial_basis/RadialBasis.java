package super_vised.radial_basis;

import com.google.common.base.Preconditions;
import common.list_arrays.List2ArrayConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;


/**
 *
 *  A Radial Basis function class, which represents a set of kernel functions and their corresponding weights.
 *  It provides methods to calculate the output of the radial basis function for a given input, as well as
 *  methods to add new kernels and retrieve existing ones.

 * In multiple methods stream could have been used instead of for loop but for loop is faster than streams
 */

@AllArgsConstructor
public class RadialBasis {

    List<KernelProperties> kernels;
    @Setter
    @Getter
    double[] weights;

    public static RadialBasis empty() {
        return new RadialBasis(new ArrayList<>(), createWeightsAllZero(0));
    }

    public static RadialBasis of(@NonNull List<KernelProperties> kernels) {
        return new RadialBasis(kernels, createWeightsAllZero(kernels.size()));
    }

    private static double[] createWeightsAllZero(int size) {
        return new double[size];
    }

    public void addKernel(KernelProperties kernel) {
        kernels.add(kernel);
        weights=createWeightsAllZero(kernels.size());
    }

    public KernelProperties getKernel(int index) {
        Preconditions.checkArgument(index >= 0 && index < nKernels(),
                "kernel index should be between 0 and " + (nKernels() - 1));
        return kernels.get(index);
    }

    public double outPut(List<Double> input) {
        return outPut(List2ArrayConverter.convertListToDoubleArr(input));
    }

    /**
     * Calculates the output of the radial basis function for the given input.
     *
     * @param input the input to the radial basis function
     * @return the output of the radial basis function
     */

    public double outPut(double[] input) {
        int nKernels = nKernels();
        Preconditions.checkArgument(nKernels > 0, "kernels should not be empty");
        Preconditions.checkArgument(nKernels == weights.length,
                "kernels size should be same as weights length");
        Preconditions.checkArgument(kernels.get(0).centerCoordinates().length == input.length,
                "input size should be same as n dimension in any kernel");
        var activations = activations(input);
        double result = 0.0;
        for (int i = 0; i < nKernels(); i++) {
            result += weights[i] * activations.get(i);
        }
        return result;
    }

    public List<Double> activations(double[] input) {
        List<Double> activations = new ArrayList<>(kernels.size());
        for (KernelProperties kernel : kernels) {
            activations.add(activation(input, kernel));
        }
        return activations;
    }

    public int nKernels() {
        return kernels.size();
    }


    public double activation(double[] input, KernelProperties kernel) {
        double distanceSquared = 0.0;
        for (int i = 0; i < input.length; i++) {
            distanceSquared += Math.pow(input[i] - kernel.centerCoordinates()[i], 2);
        }
        return Math.exp(-kernel.gamma() * distanceSquared);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (KernelProperties kernel : kernels) {
            sb.append(kernel.toString());
            sb.append(", weight=").append(weights[kernels.indexOf(kernel)]).append("\n");
        }
        return sb.toString();

    }

}
