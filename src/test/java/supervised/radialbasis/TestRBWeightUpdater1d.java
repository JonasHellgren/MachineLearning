package supervised.radialbasis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import super_vised.radial_basis.KernelProperties;
import super_vised.radial_basis.RadialBasis;
import super_vised.radial_basis.WeightUpdater;
import java.util.ArrayList;
import java.util.List;

public class TestRBWeightUpdater1d {

    public static final int GAMMA = 100;
    public static final double TOL = 1e-2;
    public static final int N_FITS = 1000;
    RadialBasis rb;

    @BeforeEach
    void init() {
        rb =  RadialBasis.empty();
        for (int i = 0; i < 10; i++) {
            double center = (double) i / 10;
            rb.addKernel(KernelProperties.of(new double[]{center}, GAMMA));
        }
    }

     @Test
      void whenUpdating_thenCorrectOutput() {
         List<Double> in1 = List.of(0.5);
         List<Double> in2 = List.of(0.9);
         double out1 = -1d;
         double out2 = 0d;
         fitWeights(in1, in2, out1, out2);
         Assertions.assertEquals(out1,rb.outPut(in1), TOL);
         Assertions.assertEquals(out2,rb.outPut(in2), TOL);
     }

    private void fitWeights(List<Double> in1, List<Double> in2, double out1, double out2) {
        var updater =  WeightUpdater.of(rb);
        for (int i = 0; i < N_FITS; i++) {
            updater.updateWeights(List.of(in1, in2), List.of(out1, out2));
        }
    }

}
