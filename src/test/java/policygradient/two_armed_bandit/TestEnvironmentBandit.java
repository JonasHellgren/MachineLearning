package policygradient.two_armed_bandit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.environments.twoArmedBandit.EnvironmentBandit;
import policy_gradient_problems.environments.twoArmedBandit.StateBandit;

import java.util.ArrayList;
import java.util.List;

 class TestEnvironmentBandit {

     static final double DELTA = 0.01;
     static final int ACTION0 = 0,ACTION1=1;
     static final StateBandit STATE = StateBandit.newDefault();
    EnvironmentBandit environment;

    @BeforeEach
     void init() {
        environment= EnvironmentBandit.newWithProbabilities(0.0,1.0);
    }

    @Test
     void givenProbabilitiesAsInInit_whenActionZero_thenWinningZeroCoins() {
        var sr=environment.step(STATE,Action.ofInteger(ACTION0));
        Assertions.assertEquals(0, sr.reward(), DELTA);
    }

    @Test
     void givenProbabilitiesAsInInit_whenActionOne_thenWinningOneCoin() {
        var sr=environment.step(STATE,Action.ofInteger(ACTION1));
        Assertions.assertEquals(1,sr.reward(), DELTA);
    }

    @Test
     void givenProbability50PercentActionZero_whenActionZeroManyTimes_thenSometimesWinningOneCoin() {
        double prob0 = 0.5;
        double prob1NotInteresting = 1.0;
        environment= EnvironmentBandit.newWithProbabilities(prob0, prob1NotInteresting);

        List<Double> rewardList=new ArrayList<>();
        for (int i = 0; i < 100 ; i++) {
            rewardList.add(environment.step(STATE,Action.ofInteger(ACTION0)).reward());
        }

        System.out.println("rewardList = " + rewardList);
        Assertions.assertTrue(rewardList.contains(0d));
        Assertions.assertTrue(rewardList.contains(1d));

    }

}
