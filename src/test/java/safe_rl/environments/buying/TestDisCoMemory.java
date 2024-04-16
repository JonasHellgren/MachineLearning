package safe_rl.environments.buying;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.memories.DisCoMemory;
import safe_rl.environments.buying_electricity.StateBuying;
import safe_rl.environments.buying_electricity.VariablesBuying;

import static common.list_arrays.ArrayUtil.isDoubleArraysEqual;
import static org.junit.jupiter.api.Assertions.*;

class TestDisCoMemory {

    public static final int N_BIAS_THETAS = 1;
    public static final double TOL = 1e-5;
    DisCoMemory<VariablesBuying> memory;
    StateI<VariablesBuying> state;

    @BeforeEach
    void init() {
        state = StateBuying.newZero();
        memory = new DisCoMemory<>(state.nContinousFeatures() + N_BIAS_THETAS);
    }

    @Test
    void whenEmpty_thenCorrect() {
        Assertions.assertEquals(0, memory.size());
        assertFalse(memory.contains(state));
    }

    @Test
    void givenEmpty_whenRead_thenCorrect() {
        double[] theta = memory.readThetas(state);
        assertTrue(isDoubleArraysEqual(new double[]{0, 0}, memory.readThetas(state), TOL));
    }

    @Test
    void whenSaveAndRead_thenCorrect() {
        double[] thetas = {1, 1};
        memory.save(state, thetas);
        assertTrue(isDoubleArraysEqual(thetas, memory.readThetas(state), TOL));
    }

    @Test
    void whenSave_thenContains() {
        memory.save(state, new double[]{1, 1});
        var stateMod=state.copy();
        stateMod.setVariables(stateMod.getVariables().withTime(2));
        assertTrue(memory.contains(state));
        assertFalse(memory.contains(stateMod));
    }

    @Test
    void whenDualSave_thenCorrect() {
        double[] theta0 = {1, 1};
        memory.save(state, theta0);
        var stateMod=state.copy();
        stateMod.setVariables(stateMod.getVariables().withTime(2));
        double[] theta2 = {2, 2};
        memory.save(stateMod, theta2);
        assertEquals(2,memory.size());
        assertTrue(isDoubleArraysEqual(theta0, memory.readThetas(state), TOL));
        assertTrue(isDoubleArraysEqual(theta2, memory.readThetas(stateMod), TOL));
    }


}
