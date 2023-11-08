package policygradient.two_armed_bandit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.twoArmedBandit.Environment;

import java.util.ArrayList;
import java.util.List;

public class TestEnvironment {

    public static final double DELTA = 0.01;
    public static final int ACTION0 = 0,ACTION1=1;
    Environment environment;

    @BeforeEach
    public void init() {
        environment=Environment.newWithProbabilities(0.0,1.0);
    }

    @Test
    public void givenProbabilitiesAsInInit_whenActionZero_thenWinningZeroCoins() {
        double reward=environment.step(ACTION0);
        Assertions.assertEquals(0,reward, DELTA);
    }

    @Test
    public void givenProbabilitiesAsInInit_whenActionOne_thenWinningOneCoin() {
        double reward=environment.step(ACTION1);
        Assertions.assertEquals(1,reward, DELTA);
    }

    @Test
    public void givenProbability50PercentActionZero_whenActionZeroManyTimes_thenSometimesWinningOneCoin() {
        double prob0 = 0.5;
        double prob1NotInteresting = 1.0;
        environment=Environment.newWithProbabilities(prob0, prob1NotInteresting);

        List<Double> rewardList=new ArrayList<>();
        for (int i = 0; i < 100 ; i++) {
            rewardList.add(environment.step(ACTION0));
        }

        System.out.println("rewardList = " + rewardList);
        Assertions.assertTrue(rewardList.contains(0d));
        Assertions.assertTrue(rewardList.contains(1d));

    }

}
