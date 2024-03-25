package policygradient.multi_coin_bandit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.environments.multicoin_bandit.EnvironmentMultiCoinBandit;
import policy_gradient_problems.environments.twoArmedBandit.EnvironmentBandit;
import policy_gradient_problems.environments.twoArmedBandit.StateBandit;

class TestEnvironmentMultiCoinBandit {

    static final int ACTION0 = 0,ACTION1=1;
    static final StateBandit STATE = StateBandit.newDefault();
    EnvironmentMultiCoinBandit environment;

    @BeforeEach
    void init() {
        environment= EnvironmentMultiCoinBandit.newWithProbabilities(0.0,1.0);
    }

    @Test
    void givenProbabilitiesAsInInit_whenActionZero_thenWinningZeroCoins() {
        var sr=environment.step(STATE, Action.ofInteger(ACTION0));
        Assertions.assertEquals(0d,sr.reward());

    }

    @Test
    void givenProbabilitiesAsInInit_whenActionOne_thenWinningCoinWithAnyValue() {
        var sr=environment.step(STATE,Action.ofInteger(ACTION1));
        Assertions.assertTrue(environment.coins().contains(sr.reward()));
        Assertions.assertNotEquals(0d,sr.reward());
    }

}
