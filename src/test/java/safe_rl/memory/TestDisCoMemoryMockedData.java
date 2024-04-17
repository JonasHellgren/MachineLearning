package safe_rl.memory;

import com.google.common.collect.Lists;
import common.list_arrays.ListUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.memories.DisCoMemory;
import safe_rl.environments.buying_electricity.StateBuying;
import safe_rl.environments.buying_electricity.VariablesBuying;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * will not converge well if N_FITS<N_FITS_PER_FEATURE
 */

public class TestDisCoMemoryMockedData {
    public static final int N_BIAS_THETAS = 1;
    public static final double TOL_VAL = 1e-2;
    public static final double ALPHA_LEARNING = 1e-1;
    public static final int N_FITS = 100;
    public static final int N_FITS_PER_FEATURE = 1;
    DisCoMemory<VariablesBuying> memory;
    StateI<VariablesBuying> state;
    List<Integer> timeList = List.of(0, 1);
    List<Double> socList = ListUtils.createDoubleListStartEndStep(0, 1, 0.1);

    @BeforeEach
    void init() {
        state = StateBuying.newZero();
        int nThetaPerKey = state.nContinousFeatures() + N_BIAS_THETAS;
        memory = new DisCoMemory<>(nThetaPerKey, ALPHA_LEARNING);
    }

    @Test
    void whenFittingMemory_thenCorrect() {
        IntStream.range(0, N_FITS).forEach(i -> fitMemory());
        System.out.println("memory = " + memory);

        Assertions.assertEquals(timeList.size(), memory.size());
        assertTimeAndSoc(0, 0);
        assertTimeAndSoc(1, 0.5);
    }

    private void fitMemory() {
        var combos = Lists.cartesianProduct(List.of(timeList,socList));
        combos.forEach(c -> {
            Integer time = (Integer) c.get(0);
            Double soc =  (Double) c.get(1);
            double value = getValue(time, soc);
            state.setVariables(VariablesBuying.newTimeSoc(time, soc));
            memory.fit(state.copy(), value, N_FITS_PER_FEATURE);
        } );
    }

    private static double getValue(Integer time, Double soc) {
        return time * 10 + soc * 1;
    }


    private void assertTimeAndSoc(int time, double soc) {
        state.setVariables(VariablesBuying.newTimeSoc(time, soc));
        double read = memory.read(state);
        Assertions.assertEquals(getValue(time, soc), read, TOL_VAL);
    }


}
