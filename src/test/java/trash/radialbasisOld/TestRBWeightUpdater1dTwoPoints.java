package book_rl_explained.radialbasisOld;

import book_rl_explained.radialbasis.KernelProperties;
import book_rl_explained.radialbasis.RadialBasis;
import book_rl_explained.radialbasis.WeightUpdaterOld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestRBWeightUpdater1dTwoPoints {

    public static final int GAMMA = 100;
    public static final double TOL = 1e-2;
    public static final int N_FITS = 1000;
    RadialBasis rb;

    @BeforeEach
    void init() {
        rb =  RadialBasis.empty();
        for (int i = 0; i < 10; i++) {
            double center = (double) i / 10;
            rb.addKernel(KernelProperties.ofGammas(new double[]{center}, new double[]{GAMMA}));
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

    @Test
    void whenUpdatingUsingError_thenCorrectOutput() {
        List<Double> in1 = List.of(0.5);
        List<Double> in2 = List.of(0.9);
        double out1 = -1d;
        double out2 = 0d;
        fitWeightsFromError(in1, in2, out1, out2);
        Assertions.assertEquals(out1,rb.outPut(in1), TOL);
        Assertions.assertEquals(out2,rb.outPut(in2), TOL);
    }


    private void fitWeights(List<Double> in1, List<Double> in2, double out1, double out2) {
        var updater =  WeightUpdaterOld.of(rb);
        for (int i = 0; i < N_FITS; i++) {
            updater.updateWeights(List.of(in1, in2), List.of(out1, out2));
        }
    }

    private void fitWeightsFromError(List<Double> in1, List<Double> in2, double out1, double out2) {
        var updater =  WeightUpdaterOld.of(rb);
        for (int i = 0; i < N_FITS; i++) {
            double err1= out1 - rb.outPut(in1);
            double err2= out2 - rb.outPut(in2);
            updater.updateWeightsFromErrors(List.of(in1, in2), List.of(err1,err2));
        }
    }


}
