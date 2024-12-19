package book_rl_explained.radial_basis;

import com.google.common.base.Preconditions;
import common.list_arrays.List2ArrayConverter;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;


/**
 * This class represents a collection of Kernel objects.
 * Each Kernel represents a radial basis function with a specific center and gamma value.
 */
@AllArgsConstructor
public class Kernels {

    List<Kernel> kernels;

    public static Kernels empty() {
        return new Kernels(new ArrayList<>());
    }

    public int size() {
        return kernels.size();
    }

    public void addKernel(Kernel kernel) {
        kernels.add(kernel);
    }

    public void addKernelsWithCentersAndSigmas(double[] centers, double[] sigmas) {
        for (int i = 0; i < centers.length; i++) {
            addKernel(Kernel.ofSigmas(new double[]{centers[i]}, new double[]{sigmas[i]}));
        }
    }

    public Kernel get(int index) {
        Preconditions.checkArgument(index >= 0 && index < nKernels(),
                "kernel index should be between 0 and " + (nKernels() - 1));
        return kernels.get(index);
    }

    private int nKernels() {
        return kernels.size();
    }

    public List<Double> getActivationOfSingleInput(List<Double> input) {
        Preconditions.checkArgument(size()>0
                , "kernels should not be empty");
        Preconditions.checkArgument(input.size() == kernels.get(0).nDimensions()
                ,"input size should be same as kernels dimensions");
        List<Double> activations = new ArrayList<>();
        for (Kernel kernel : kernels) {
            var inputArr = List2ArrayConverter.convertListToDoubleArr(input);
            activations.add(kernel.activation(inputArr));
        }
        return activations;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (Kernel kernel : kernels) {
            sb.append(kernel).append("\n");
        }
        return "\n" + sb;
    }

}
