package book_rl_explained.radialbasisOld;

import book_rl_explained.radialbasis.RadialBasis;
import book_rl_explained.radialbasis.WeightUpdaterOld;
import com.beust.jcommander.internal.Lists;
import org.hellgren.utilities.list_arrays.ArrayCreator;
import org.hellgren.utilities.list_arrays.ListCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestRBWeightUpdater1dLine {
    public static final double TOL = 1.0;
    public static final int N_FITS = 1000;
    public static final double MAX_X = 10d;
    public static final int N_KERNELS = 6;
    public static final double SIGMA =(MAX_X/(N_KERNELS-1));
    RadialBasis rb;
    List<List<Double>> inList;
    List<Double> outList;

    @BeforeEach
    void init() {
        rb = RadialBasis.empty();
        double[] centers = ArrayCreator.createArrayFromStartAndEndWithNofItems(0d, MAX_X, N_KERNELS);
        double[] sigmas = ArrayCreator.createArrayWithSameDoubleNumber(N_KERNELS, SIGMA);
        rb.addKernelsWithCentersAndSigmas(centers, sigmas);
        var in0 = ListCreator.createFromStartToEndWithNofItems(0d, MAX_X, 10);
        inList = Lists.newArrayList();
        for (double in : in0) {
            inList.add(List.of(in));
        }
        outList = ListCreator.createFromStartToEndWithNofItems(0d, MAX_X, 10);

    }

    @Test
    void whenUpdating_thenCorrectOutput() {
        fitWeights();
        assertRbOut();
    }

    @Test
    void whenUpdatingUsingError_thenCorrectOutput() {
        fitWeightsFromError();
        assertRbOut();
    }

    private void assertRbOut() {
        for (int idx = 0; idx < inList.size(); idx++) {
            Assertions.assertEquals(outList.get(idx), rb.outPut(inList.get(idx)), TOL);
        }
    }


    private void fitWeights() {
        var updater = WeightUpdaterOld.of(rb);
        for (int i = 0; i < N_FITS; i++) {
            updater.updateWeights(inList, outList);
        }
    }

    private void fitWeightsFromError() {
        var updater = WeightUpdaterOld.of(rb);
        for (int i = 0; i < N_FITS; i++) {
            List<Double> errors = new ArrayList<>(Collections.nCopies(inList.size(), 0.0));
            for (int idx = 0; idx < inList.size(); idx++) {
                errors.set(idx, outList.get(idx) - rb.outPut(inList.get(idx)));
            }
            updater.updateWeightsFromErrors(inList, errors);
        }
    }


}
