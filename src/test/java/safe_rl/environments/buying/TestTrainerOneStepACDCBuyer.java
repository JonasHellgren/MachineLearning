package safe_rl.environments.buying;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.trainers.TrainerOneStepACDC;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.*;
import safe_rl.environments.factories.FactoryOptModel;
import safe_rl.helpers.EpisodeInfo;

/***
 * Optimal behavior in this test is max power two first steps
 *
 * critical params:  targetMean(2d).targetStd(3d).targetCritic(0d)
 */

public class TestTrainerOneStepACDCBuyer {

    public static final double SOC_START = 0.2;
    public static final double TOL_POWER = 0.5;
    public static final double SOC_END = 1.0;

    BuySettings settings3hours;
    TrainerOneStepACDC<VariablesBuying> trainer;

    @BeforeEach
    void init() {
        settings3hours = BuySettings.new3HoursSamePrice();
        var environment = new EnvironmentBuying(settings3hours);
        var startState = StateBuying.of(VariablesBuying.newSoc(SOC_START));
        var safetyLayer = new SafetyLayer<VariablesBuying>(FactoryOptModel.createChargeModel(settings3hours));
        var agent=AgentACDCSafeBuyer.builder()
                .settings(settings3hours)
                .targetMean(2d).targetLogStd(Math.log(3d)).targetCritic(0d)
                .learningRateActorMean(1e-2).learningRateActorStd(1e-3).learningRateCritic(1e-1)
                .state(startState)
                .build();
        var trainerParameters=TrainerParameters.newDefault()
                .withNofEpisodes(2000).withGamma(1.0).withRatioPenCorrectedAction(2d);
        trainer= TrainerOneStepACDC.<VariablesBuying>builder()
                .environment(environment).agent(agent)
                .safetyLayer(safetyLayer)
                .trainerParameters(trainerParameters)
                .startStateSupplier(() -> startState.copy() )
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
        double powerExpected = settings3hours.powerBattMax();

        Assertions.assertEquals(powerExpected,powerList.get(0), TOL_POWER);
        Assertions.assertEquals(settings3hours.priceEnd()*(SOC_END- SOC_START)* settings3hours.energyBatt(),vc2,10);
        Assertions.assertEquals(powerExpected,powerList.get(0), TOL_POWER);
        Assertions.assertEquals(powerExpected,va0, TOL_POWER);

    }



}
