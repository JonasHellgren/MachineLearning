package safe_rl.environments.buying;

import common.math.MathUtils;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.environments.buying_electricity.AgentACDCSafeBuyer;
import safe_rl.environments.buying_electricity.BuySettings;
import safe_rl.environments.buying_electricity.StateBuying;
import safe_rl.environments.buying_electricity.VariablesBuying;

public class TestAgentACDCSafeBuyer {

    public static final double TOL = 1e-1;
    public static final double TARGET_MEAN = 1d;
    public static final double TARGET_STD = 0.5;
    public static final double ADV = 1d;
    public static final double TARGET_CRITIC = 0d;
    AgentACDCSafeBuyer agent;

    @BeforeEach
    void init() {
        agent=AgentACDCSafeBuyer.builder()
                .settings(BuySettings.new5HoursIncreasingPrice())
                .targetMean(TARGET_MEAN).targetStd(TARGET_STD).targetCritic(TARGET_CRITIC)
                .state(StateBuying.newZero())
                .build();
    }

    @Test
    void whenInit_thenCanRead() {
        var meanStd=agent.readActor();
        var value=agent.readCritic();

        System.out.println("meanStd = " + meanStd);
        System.out.println("value = " + value);

        Assertions.assertEquals(TARGET_MEAN,meanStd.getFirst(), TOL);
        Assertions.assertEquals(TARGET_STD,meanStd.getSecond(), TOL);
        Assertions.assertEquals(0,value, TOL);

    }

    @Test
    void whenChooseAction_thenCanRead() {
        double action=agent.chooseAction().asDouble();
        Assertions.assertTrue(MathUtils.isInRange(action,-1,2));
    }

    @Test
    void whenActionEqualToMean_thenCorrectGradLog() {
        var ms0=agent.readActor();
        var gradLog=agent.fitActor(Action.ofDouble(TARGET_MEAN), ADV);
        var ms=agent.readActor();
        somePrinting(ms0, gradLog, ms);
        Assertions.assertEquals(0,gradLog.getFirst(),1e-2);
        Assertions.assertTrue(gradLog.getSecond()<0);
        Assertions.assertNotEquals(ms0,ms);
    }


    @Test
    void whenActionFarBelowMean_thenCorrectGradLog() {
        var ms0=agent.readActor();
        var gradLog=agent.fitActor(Action.ofDouble(TARGET_MEAN-0.8), ADV);
        var ms=agent.readActor();
        Assertions.assertTrue(gradLog.getFirst()<0);  //decrease mean
        Assertions.assertTrue(gradLog.getSecond()>0);  //far of -> increase std
        Assertions.assertNotEquals(ms0,ms);
    }

    @Test
    void whenActionFarAboveMean_thenCorrectGradLog() {
        var ms0=agent.readActor();
        var gradLog=agent.fitActor(Action.ofDouble(TARGET_MEAN+0.8), ADV);
        var ms=agent.readActor();
        Assertions.assertTrue(gradLog.getFirst()>0); //increase mean
        Assertions.assertTrue(gradLog.getSecond()>0);
        Assertions.assertNotEquals(ms0,ms);

    }

    @Test
    void whenFitCriticWhenCorrect() {
        double v0=agent.readCritic();
        agent.fitCritic(1);
        double v1=agent.readCritic();
        agent.setState(StateBuying.of(VariablesBuying.newTimeSoc(2,0)));
        double vTime2=agent.readCritic();
        Assertions.assertEquals(TARGET_CRITIC,v0);
        Assertions.assertNotEquals(v0,v1);
        Assertions.assertEquals(v0,vTime2);
    }


    private static void somePrinting(Pair<Double, Double> ms0, Pair<Double, Double> gradLog, Pair<Double, Double> ms) {
        System.out.println("gradLog = " + gradLog);
        System.out.println("ms0 = " + ms0 +", ms = " + ms);
    }

}
