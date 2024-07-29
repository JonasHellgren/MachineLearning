package safe_rl.environments.buying;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.agent.AgentACDCSafe;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.trainer.TrainerOneStepACDC;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.environments.buying_electricity.*;
import safe_rl.environments.factories.FactoryOptModel;
import safe_rl.domain.trainer.helpers.EpisodeInfo;

/***
 * Optimal behavior in this test is max power two first steps
 *
 * critical params:  targetMean(2d).targetStd(3d).targetCritic(0d)
 */

public class TestTrainerOneStepACDCBuyer {

    public static final double SOC_START = 0.2;
    public static final double TOL_POWER = 0.8;
    public static final double SOC_END = 1.0;

    SettingsBuying settings3hours;
    TrainerOneStepACDC<VariablesBuying> trainer;

    @BeforeEach
    void init() {
        settings3hours = SettingsBuying.new3HoursSamePrice();
        var environment = new EnvironmentBuying(settings3hours);
        var startState = StateBuying.of(VariablesBuying.newSoc(SOC_START));
        var safetyLayer = new SafetyLayer<>(FactoryOptModel.createChargeModel(settings3hours));
        var agent= AgentACDCSafe.<VariablesBuying>builder()
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
    @SneakyThrows
    void whenTrained_thenCorrect() {

        trainer.train();
        var experiences = trainer.evaluate();

        var ei=new EpisodeInfo<>(experiences);
        var powerList=ei.actionsApplied();
        var ac=(AgentACDCSafe) trainer.getAgent();
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
