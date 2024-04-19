package safe_rl.environments.buying;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.trainers.TrainerOneStepACDC;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.*;
import safe_rl.helpers.EpisodeInfo;

/***
 * Optimal behavior in this test is max power two first steps
 *
 * critical params:  targetMean(2d).targetStd(3d).targetCritic(0d)
 */

public class TestTrainerACDCBuyer {

    public static final double SOC_START = 0.2;
    public static final double TOL_POWER = 1e-1;
    public static final double SOC_END = 1.0;

    BuySettings settings3;
    TrainerOneStepACDC<VariablesBuying> trainer;

    @BeforeEach
    void init() {
        settings3 = BuySettings.new3HoursSamePrice();
        var environment = new EnvironmentBuying(settings3);
        var startState = StateBuying.of(VariablesBuying.newSoc(SOC_START));
        var safetyLayer = new SafetyLayerBuying<VariablesBuying>(settings3);
        var agent=AgentACDCSafeBuyer.builder()
                .settings(settings3)
                .targetMean(2d).targetLogStd(Math.log(3d)).targetCritic(0d)
                .learningRateActorMean(1e-2).learningRateActorStd(0e-1).learningRateCritic(1e-1)
                .state(startState)
                .build();
        var trainerParameters=TrainerParameters.newDefault()
                .withNofEpisodes(1000).withGamma(1.0);
        trainer= TrainerOneStepACDC.<VariablesBuying>builder()
                .environment(environment).agent(agent)
                .safetyLayer(safetyLayer)
                .trainerParameters(trainerParameters)
                .startState(startState)
                .build();
    }

    @Test
    void whenTrained_thenCorrect() {

        trainer.train();
        var experiences = trainer.evaluate();

        var ei=new EpisodeInfo<>(experiences);
        var powerList=ei.actionsApplied();
        var ac=(AgentACDCSafeBuyer) trainer.getAgent();
        var memMean=ac.getActorMean();
        var memCrit=ac.getCritic();
        double vc2=memCrit.read(StateBuying.of(VariablesBuying.newTimeSoc(2, SOC_END)));
        double va0=memMean.read(StateBuying.of(VariablesBuying.newTime(0)));
        double powerExpected = settings3.powerBattMax();

        Assertions.assertEquals(powerExpected,powerList.get(0), TOL_POWER);
        Assertions.assertEquals(settings3.priceEnd()*(SOC_END- SOC_START)*settings3.energyBatt(),vc2,10);
        Assertions.assertEquals(powerExpected,powerList.get(0), TOL_POWER);
        Assertions.assertEquals(powerExpected,va0, TOL_POWER);

    }



}
