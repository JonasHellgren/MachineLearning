package supervised.radialbasis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import super_vised.radial_basis.KernelProperties;
import super_vised.radial_basis.RadialBasis;
import super_vised.radial_basis.WeightUpdater;
import java.util.ArrayList;
import java.util.List;

public class TestRBWeightUpdater2d {
    public static final int GAMMA = 100;
    public static final double TOL = 1e-2;
    public static final int N_FITS = 1000;
    RadialBasis rb;

    @BeforeEach
    void init() {
        var kernels = new ArrayList<KernelProperties>();
        for (int x = 0; x < 10; x++) {
            double centerX = (double) x / 10;
            for (int y = 0; y < 10; y++) {
                double centerY = (double) y / 10;
                kernels.add(KernelProperties.of(new double[]{centerX, centerY}, GAMMA));
            }

        }
        rb =  RadialBasis.of(kernels);
    }

    @Test
    void whenUpdating_thenCorrectOutput() {
        var in1 = List.of(0.5, 0.5);
        var in2 = List.of(0.9, 0.9);
        double out1 = -1d;
        double out2 = 0d;
        fitWeights(List.of(in1, in2), List.of(out1, out2));
        Assertions.assertEquals(out1, rb.outPut(in1), TOL);
        Assertions.assertEquals(out2, rb.outPut(in2), TOL);
    }

    private void fitWeights(List<List<Double>> inputList, List<Double> outputList) {
        var updater =  WeightUpdater.of(rb);
        for (int i = 0; i < N_FITS; i++) {
            updater.updateWeights(inputList, outputList);
        }
    }


}
