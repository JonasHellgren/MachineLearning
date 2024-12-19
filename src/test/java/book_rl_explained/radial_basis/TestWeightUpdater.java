package book_rl_explained.radial_basis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestWeightUpdater {


    public static final double LEARNING_RATE = 0.5;

    @Test
    void given1Kernel_testUpdateWeights() {
        TrainData data = TrainData.ofErrors(
                List.of(List.of(1.0), List.of(3.0)),
                List.of(1.0, 1.0));
        Kernels kernels = Kernels.empty();
        Kernel kernel = Kernel.ofSigmas(new double[]{1.0}, new double[]{1.0});
        kernels.addKernel(kernel);
        Weights weights = Weights.allZero(kernels.size());
        Activations activations = Activations.of(data.nSamples());
        activations.calcuteActivations(data, kernels);

        var updater = WeightUpdater.of(LEARNING_RATE);
        updater.updateWeights(data, weights, activations);

        Assertions.assertEquals(1, kernels.size());
        Assertions.assertEquals(1, weights.size());
        Assertions.assertTrue(weights.get(0) > 0);
    }


    @Test
    void given2Kernels_testUpdateWeights() {
        TrainData data = TrainData.ofErrors(
                List.of(List.of(0.0), List.of(1.0)),
                List.of(1.0, 1.0));
        Kernels kernels = Kernels.empty();
        Kernel kernel1 = Kernel.ofSigmas(new double[]{0.0}, new double[]{1.0});
        Kernel kernel2 = Kernel.ofSigmas(new double[]{1.0}, new double[]{1.0});
        kernels.addKernel(kernel1);
        kernels.addKernel(kernel2);
        Weights weights = Weights.allZero(kernels.size());
        Activations activations = Activations.of(data.nSamples());
        activations.calcuteActivations(data, kernels);

        var updater = WeightUpdater.of(LEARNING_RATE);
        updater.updateWeights(data, weights, activations);

        System.out.println("weights = " + weights);

        Assertions.assertEquals(2, kernels.size());
        Assertions.assertEquals(2, weights.size());
        Assertions.assertTrue(weights.get(0) > 0);
        Assertions.assertTrue(weights.get(1) > 0);
    }


}
