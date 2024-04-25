package safe_rl.environments.buying;

import common.math.MathUtils;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.environments.buying_electricity.AgentACDCSafe;
import safe_rl.environments.buying_electricity.SettingsBuying;
import safe_rl.environments.buying_electricity.StateBuying;
import safe_rl.environments.buying_electricity.VariablesBuying;

public class TestAgentACDCSafeBuyer {

    public static final double TOL = 1e-1;
    public static final double TARGET_MEAN = 1d;
    public static final double TARGET_STD = 0.5;
    public static final double LOG_STD_TAR = Math.log(TARGET_STD);
    public static final double ADV = 1d;
    public static final double TARGET_CRITIC = 0d;
    public static final double TOL_GRAD_LOG = 1e-1;
    AgentACDCSafe<VariablesBuying> agent;
    StateBuying state0;

    @BeforeEach
    void init() {
        state0 = StateBuying.newZero();
        agent= AgentACDCSafe.<VariablesBuying>builder()
                .settings(SettingsBuying.new5HoursIncreasingPrice())
                .targetMean(TARGET_MEAN).targetLogStd(LOG_STD_TAR).targetCritic(TARGET_CRITIC)
                .state(state0)
                .build();
    }

    @Test
    void whenInit_thenCanRead() {
        var meanStd=agent.readActor(state0);
        var value=agent.readCritic(StateBuying.newZero());

        Assertions.assertEquals(TARGET_MEAN,meanStd.getFirst(), TOL);
        Assertions.assertEquals(TARGET_STD,meanStd.getSecond(), TOL);
        Assertions.assertEquals(0,value, TOL);

    }

    @Test
    void whenChooseAction_thenCanRead() {
        double action=agent.chooseAction(state0).asDouble();
        System.out.println("action = " + action);
        Assertions.assertTrue(MathUtils.isInRange(action,-1,2));
    }

    @Test
    void whenActionEqualToMean_thenCorrectGradLog() {
        var ms0=agent.readActor(state0);
        var gradMAndLogS=agent.fitActor(StateBuying.newZero(),Action.ofDouble(TARGET_MEAN), ADV);
        var ms=agent.readActor(state0);
        double derStd = getDerStd(gradMAndLogS);
        somePrinting(ms0, gradMAndLogS, ms);
        Assertions.assertEquals(0,gradMAndLogS.getFirst(), TOL_GRAD_LOG);
        Assertions.assertTrue(derStd<0);
        Assertions.assertNotEquals(ms0,ms);
    }

    @Test
    void whenActionFarBelowMean_thenCorrectGradLog() {
        var ms0=agent.readActor(state0);
        var gradLog=agent.fitActor(StateBuying.newZero(),Action.ofDouble(TARGET_MEAN-0.8), ADV);
        var ms=agent.readActor(state0);
        Assertions.assertTrue(gradLog.getFirst()<0);  //decrease mean
        Assertions.assertTrue(gradLog.getSecond()>0);  //far of -> increase std
        Assertions.assertNotEquals(ms0,ms);
    }

    @Test
    void whenActionFarAboveMean_thenCorrectGradLog() {
        var ms0=agent.readActor(state0);
        var gradLog=agent.fitActor(StateBuying.newZero(),Action.ofDouble(TARGET_MEAN+0.8), ADV);
        var ms=agent.readActor(state0);
        Assertions.assertTrue(gradLog.getFirst()>0); //increase mean
        Assertions.assertTrue(gradLog.getSecond()>0);
        Assertions.assertNotEquals(ms0,ms);
    }

    @Test
    void whenFitCriticWhenCorrect() {
        double v0=agent.readCritic(state0);
        agent.fitCritic(StateBuying.newZero(),1);
        double v1=agent.readCritic(state0);
        StateBuying state2 = StateBuying.of(VariablesBuying.newTimeSoc(2, 0));
        double vTime2=agent.readCritic(state2);
        Assertions.assertEquals(TARGET_CRITIC,v0);
        Assertions.assertNotEquals(v0,v1);
        Assertions.assertEquals(v0,vTime2);
    }


    private static void somePrinting(Pair<Double, Double> ms0, Pair<Double, Double> gradLog, Pair<Double, Double> ms) {
        System.out.println("gradLog = " + gradLog);
        System.out.println("ms0 = " + ms0 +", ms = " + ms);
    }


    private double getDerStd(Pair<Double, Double> gradMAndLogS) {
        return agent.readActor(state0).getSecond()* gradMAndLogS.getSecond();
    }


}
