package safe_rl.domain;

import com.beust.jcommander.internal.Lists;
import common.list_arrays.ListUtils;
import common.other.RandUtils;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.agent.AgentACDCSafe;
import safe_rl.domain.agent.aggregates.DisCoMemory;
import safe_rl.domain.trainer.aggregates.ReplayBufferMultiStepExp;
import safe_rl.domain.trainer.aggregates.FitterUsingReplayBuffer;
import safe_rl.domain.trainer.value_objects.MultiStepResultItem;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.domain.agent.aggregates.DisCoMemoryInitializer;

import java.util.List;

import static common.list_arrays.ListUtils.doublesStartEndStep;

public class TestFitterUsingReplayBuffer {

    public static final double SOC_MIN = 0d;
    public static final double SOC_MAX = 1d;
    public static final int MAX_SIZE = 100;
    public static final int T_END = 5;
    public static final int T_TERM = T_END + 1;
    public static final int TAR_VALUE_INIT = 0;
    public static final int STD_TAR = 0;
    public static final double TOL_VALUE = 1;
    public static final double LEARNING_RATE_REPLAY_BUFFER_CRITIC = 1e-1;
    public static final int N_ASSERTS = 10;
    public static final int STEP_HORIZON = 3;
    SettingsTrading settingsTrading;
    FitterUsingReplayBuffer<VariablesTrading> fitter;
    ReplayBufferMultiStepExp<VariablesTrading> buffer;
    DisCoMemory<VariablesTrading> critic;

    @BeforeEach
    void init() {

        var paramsTrainer = TrainerParameters.newDefault().withStepHorizon(STEP_HORIZON)
                .withLearningRateReplayBufferCritic(LEARNING_RATE_REPLAY_BUFFER_CRITIC);

        buffer = ReplayBufferMultiStepExp.newFromMaxSize(MAX_SIZE);
        for (int i = 0; i < MAX_SIZE; i++) {
            var experience = getExperience(paramsTrainer);
            buffer.add(experience);
        }
        settingsTrading = SettingsTrading.new5HoursIncreasingPrice();
        StateTrading state = StateTrading.newFullAndFresh();
        AgentACDiscoI<VariablesTrading> agent= AgentACDCSafe.<VariablesTrading>builder()
                .settings(settingsTrading)
                .targetMean(0.0d).targetLogStd(Math.log(3d)).targetCritic(0d).absActionNominal(1d)
                .learningRateActorMean(1e-2).learningRateActorStd(1e-2).learningRateCritic(1e-3)
                .gradMaxActor0(1d).gradMaxCritic0(1d)
                .state(state.copy())
                .build();
        critic=agent.getCritic();
        var initializer = getInitializer(state, critic, TAR_VALUE_INIT, STD_TAR);
        initializer.initialize();
        fitter = new FitterUsingReplayBuffer<>(agent, paramsTrainer,StateTrading.INDEX_SOC);
    }


    @Test
    void whenFitting_thenCorrect() {
        for (int i = 0; i < 10000; i++) {
            fitter.fit(buffer);
        }

        System.out.println("critic = " + critic);
        for (int i = 0; i < N_ASSERTS; i++) {
            double soc = RandUtils.randomNumberBetweenZeroAndOne();
            int randomTime = getRandomTime();
            Assertions.assertEquals(
                    getaRewardForSoc(soc),
                    critic.read(StateTrading.of(VariablesTrading.newTimeSoc(randomTime, soc))), TOL_VALUE);
        }
    }


    private static MultiStepResultItem<VariablesTrading> getExperience(TrainerParameters paramsTrainer) {
        int time = getRandomTime();
        double soc = RandUtils.randomNumberBetweenZeroAndOne();
        boolean isTerminal;
        List<Double> rewardList = Lists.newArrayList();
        List<Action> actionList = Lists.newArrayList();
        StateI<VariablesTrading> state0 = StateTrading.of(VariablesTrading.newTimeSoc(time, soc)).copy();
        int n = 0;
        int timeNext;
        do {
            double action = RandUtils.randomNumberBetweenZeroAndOne() - 0.5;
            timeNext = time + 1;
            isTerminal = timeNext > T_END;
            double reward = isTerminal ? getaRewardForSoc(soc) : TAR_VALUE_INIT;
            rewardList.add(reward);
            actionList.add(Action.ofDouble(action));
            time++;
            n++;
        } while (!isTerminal && n < paramsTrainer.stepHorizon());

        var stateFuture = stateFutureNotApplicable(paramsTrainer, isTerminal, n)
                ? null
                : StateTrading.of(VariablesTrading.newTimeSoc(timeNext, soc)).copy();

        return MultiStepResultItem.of(state0, Action.ofDouble(0d), ListUtils.sumList(rewardList),stateFuture,isTerminal);

                /*
                ExperienceMultiStep.<VariablesTrading>builder()
                .state(state0)
                .actions(actionList)
                .rewards(rewardList)
                .stateFuture(stateFuture)
                .isStateFutureTerminalOrNotPresent(isTerminal)
                .build();*/
    }

    private static int getRandomTime() {
        return RandUtils.getRandomIntNumber(TAR_VALUE_INIT, T_TERM); //T_TERM exclusive
    }

    private static double getaRewardForSoc(double soc) {
        return soc * N_ASSERTS;
    }

    private static boolean stateFutureNotApplicable(TrainerParameters paramsTrainer, boolean isTerminal, int n) {
        return isTerminal || n != paramsTrainer.stepHorizon();
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
