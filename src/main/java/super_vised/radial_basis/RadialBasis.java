package super_vised.radial_basis;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class RadialBasis {

    List<KernelProperties> kernels;

    public void addKernel(KernelProperties kernel) {
        kernels.add(kernel);
    }

    public double output(double[] input, double[] weights) {
        Preconditions.checkArgument(nKernels() == weights.length, "kernels size should be same as weights length");
        var activations=activations(input);
        double result = 0.0;
        for (int i = 0; i < nKernels(); i++) {
            result += weights[i] * activations.get(i);
        }
        return result;
    }

    public List<Double> activations(double[] input) {
        List<Double> activations=new ArrayList<>(nKernels());
        for (int i = 0; i < nKernels(); i++) {
            KernelProperties kernel = kernels.get(i);
            activations.add(activation(input, kernel));
        }
        return activations;
    }

    public int nKernels() {
        return kernels.size();
    }


    private double activation(double[] x, KernelProperties kernel) {
        double distanceSquared = 0.0;
        for (int i = 0; i < x.length; i++) {
            distanceSquared += Math.pow(x[i] - kernel.centerCoordinates()[i], 2);
        }
        return Math.exp(-kernel.gamma() * distanceSquared);
    }

}
