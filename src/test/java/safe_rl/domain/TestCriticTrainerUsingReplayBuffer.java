package safe_rl.domain;

import common.math.MathUtils;
import common.other.RandUtils;
import org.junit.jupiter.api.BeforeEach;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.memories.ReplayBuffer;
import safe_rl.domain.trainers.CriticFitterUsingReplayBuffer;
import safe_rl.domain.value_classes.Experience;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;

public class TestCriticTrainerUsingReplayBuffer {


    public static final int MAX_SIZE = 100;
    public static final int T_END = 5;
    CriticFitterUsingReplayBuffer<VariablesTrading> trainer;
    ReplayBuffer<VariablesTrading> buffer;

    @BeforeEach
    void init() {

        buffer = ReplayBuffer.newFromMaxSize(MAX_SIZE);
        for (int i = 0; i < MAX_SIZE; i++) {

            int time = RandUtils.getRandomIntNumber(0, T_END);
            double soc = RandUtils.randomNumberBetweenZeroAndOne();
            double action = RandUtils.randomNumberBetweenZeroAndOne() - 0.5;
            int timeNext = time + 1;
            double socNext = MathUtils.clip(soc + action / 10, 0, 1);
            boolean isTerminal = timeNext > T_END;
            double reward = isTerminal ? soc * 10 : 0;

            var experience = Experience.notSafeCorrected(
                    StateTrading.of(VariablesTrading.newTimeSoc(time, soc)),
                    Action.ofDouble(action),
                    reward,
                    StateTrading.of(VariablesTrading.newTimeSoc(timeNext, socNext)),
                    isTerminal);
            buffer.addExperience(experience);
        }

    }



}
