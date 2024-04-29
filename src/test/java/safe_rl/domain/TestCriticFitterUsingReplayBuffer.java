package safe_rl.domain;

import common.other.RandUtils;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.memories.DisCoMemory;
import safe_rl.domain.memories.ReplayBuffer;
import safe_rl.domain.trainers.CriticFitterUsingReplayBuffer;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.helpers.DisCoMemoryInitializer;

import java.util.List;

import static common.list_arrays.ListUtils.doublesStartEndStep;
import static common.list_arrays.ListUtils.merge;

public class TestCriticFitterUsingReplayBuffer {

    public static final double SOC_MIN = 0d;
    public static final double SOC_MAX = 1d;
    public static final int MAX_SIZE = 100;
    public static final int T_END = 5;
    public static final int T_TERM = T_END+1;
    public static final int TAR_VALUE_INIT = 0;
    public static final int STD_TAR = 0;
    SettingsTrading settingsTrading;
    CriticFitterUsingReplayBuffer<VariablesTrading> fitter;
    ReplayBuffer<VariablesTrading> buffer;
    DisCoMemory<VariablesTrading> critic;

    @BeforeEach
    void init() {

        buffer = ReplayBuffer.newFromMaxSize(MAX_SIZE);
        for (int i = TAR_VALUE_INIT; i < MAX_SIZE; i++) {

            int time = RandUtils.getRandomIntNumber(TAR_VALUE_INIT, T_TERM);
            double soc = RandUtils.randomNumberBetweenZeroAndOne();
            double action = RandUtils.randomNumberBetweenZeroAndOne() - 0.5;
            int timeNext = time + 1;
            boolean isTerminal = timeNext > T_END;
            double reward = isTerminal ? soc * 10 : TAR_VALUE_INIT;

            var experience = Experience.notSafeCorrected(
                    StateTrading.of(VariablesTrading.newTimeSoc(time, soc)),
                    Action.ofDouble(action),
                    reward,
                    StateTrading.of(VariablesTrading.newTimeSoc(timeNext, TAR_VALUE_INIT)),
                    isTerminal)
                    .withValue(reward);
            buffer.addExperience(experience);

            settingsTrading=SettingsTrading.new5HoursIncreasingPrice();
            StateTrading state = StateTrading.newFullAndFresh();
            critic = new DisCoMemory<>(state.nContinousFeatures() + 1);
            var initializer=getInitializer(state,critic, TAR_VALUE_INIT, STD_TAR);
            initializer.initialize();
            var paramsTrainer= TrainerParameters.newDefault();
            fitter=new CriticFitterUsingReplayBuffer<>(critic,paramsTrainer);
        }
    }

    @Test
    void whenFitting_thenCorrect() {
        fitter.fit(buffer);


        System.out.println("critic = " + critic);


    }




    private DisCoMemoryInitializer<VariablesTrading> getInitializer(StateTrading state,
                                                                   DisCoMemory<VariablesTrading> memory1,
                                                                   double tarValue,
                                                                   double stdTar) {
        return DisCoMemoryInitializer.<VariablesTrading>builder()
                .memory(memory1)
                .discreteFeatSet(List.of(
                        doublesStartEndStep(TAR_VALUE_INIT, settingsTrading.timeEnd(), settingsTrading.dt())))
                .contFeatMinMax(Pair.create(List.of(SOC_MIN), List.of(SOC_MAX)))
                .valTarMeanStd(Pair.create(tarValue, stdTar))
                .state(state)
                .build();
    }




}
