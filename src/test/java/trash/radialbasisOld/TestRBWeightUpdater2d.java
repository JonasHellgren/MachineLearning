package book_rl_explained.radialbasisOld;

import book_rl_explained.radialbasis.KernelProperties;
import book_rl_explained.radialbasis.RadialBasis;
import book_rl_explained.radialbasis.WeightUpdaterOld;
import common.list_arrays.ListCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestRBWeightUpdater2d {
    public static final double GAMMAX = 100;
    public static final double GAMMAY = 20;  //for ex 100 will not work, wider activation because larger dist between centers
    public static final double TOL = 1e-2;
    public static final int N_FITS = 1000;
    RadialBasis rb;

    @BeforeEach
    void init() {
        rb =  RadialBasis.empty();
        List<Double> xList= ListCreator.createFromStartToEndWithNofItems(0,1,10);
        List<Double> yList= ListCreator.createFromStartToEndWithNofItems(0,1,3);
        for (double centerX:xList) {
            for (double centerY:yList) {
                rb.addKernel(KernelProperties.ofGammas(new double[]{centerX, centerY}, new double[]{GAMMAX,GAMMAY}));
            }
        }
    }

    @Test
    void whenUpdating_thenCorrectOutput() {
        var in1 = List.of(0.5, 0.5);
        var in2 = List.of(0.9, 0.9);
        var in3 = List.of(0.9, 0.7);
        double out1 = -1d;
        double out2 = 0d;
        double out3 = 1d;
        fitWeights(List.of(in1, in2,in3), List.of(out1, out2,out3));

        System.out.println("rb.outPut(in1) = " + rb.outPut(in1));
        System.out.println("rb.outPut(in2) = " + rb.outPut(in2));
        System.out.println("rb.outPut(in3) = " + rb.outPut(in3));

        Assertions.assertEquals(out1, rb.outPut(in1), TOL);
        Assertions.assertEquals(out2, rb.outPut(in2), TOL);
        Assertions.assertEquals(out3, rb.outPut(in3), TOL);
    }

    private void fitWeights(List<List<Double>> inputList, List<Double> outputList) {
        var updater =  WeightUpdaterOld.of(rb);
        for (int i = 0; i < N_FITS; i++) {
            updater.updateWeights(inputList, outputList);
        }
    }


}
