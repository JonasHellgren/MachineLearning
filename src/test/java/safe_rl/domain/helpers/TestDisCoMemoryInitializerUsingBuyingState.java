package safe_rl.domain.helpers;

import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.agent.aggregates.DisCoMemory;
import safe_rl.domain.agent.aggregates.DisCoMemoryInitializer;
import safe_rl.environments.buying_electricity.SettingsBuying;
import safe_rl.environments.buying_electricity.StateBuying;
import safe_rl.environments.buying_electricity.VariablesBuying;

import java.util.Arrays;
import java.util.List;

import static common.list_arrays.ListUtils.doublesStartEndStep;

class TestDisCoMemoryInitializerUsingBuyingState {

    public static final double TAR_VALUE = 10d;
    public static final double TOL_VALUE_FITTING = 1e-3;
    public static final double SOC_MIN = 0d;
    public static final double SOC_MAX = 1d;
    DisCoMemory<VariablesBuying> memory;
    SettingsBuying settings;
    DisCoMemoryInitializer<VariablesBuying> initializer;

    @BeforeEach
    void init() {
        var state = StateBuying.newZero();
        settings = SettingsBuying.new5HoursIncreasingPrice();
        memory = new DisCoMemory<>(state.nContinuousFeatures() + 1);
        initializer = getInitializer(state, memory, TAR_VALUE, 0d);
    }

    private DisCoMemoryInitializer<VariablesBuying> getInitializer(StateBuying state,
                                                                   DisCoMemory<VariablesBuying> memory1,
                                                                   double tarValue,
                                                                   double stdTar) {
        return DisCoMemoryInitializer.<VariablesBuying>builder()
                .memory(memory1)
                .discreteFeatSet(List.of(
                        doublesStartEndStep(0, settings.timeEnd(), settings.dt())))
                .contFeatMinMax(Pair.create(List.of(SOC_MIN), List.of(SOC_MAX)))
                .valTarMeanStd(Pair.create(tarValue, stdTar))
                .state(state)
                .build();
    }

    @Test
    void whenFitted_thenCorrect() {

        initializer.initialize();
        System.out.println("memory = " + memory);
        StateBuying state = StateBuying.newZero();
        System.out.println("stateNew.continousFeatures() = " + Arrays.toString(state.continuousFeatures()));

        //Assertions.assertEquals(settings.energyPriceTraj().length,memory.size());
        Assertions.assertEquals(TAR_VALUE,memory.read(state),TOL_VALUE_FITTING*10);


    }

}
