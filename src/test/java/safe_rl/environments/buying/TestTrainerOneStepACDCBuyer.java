package safe_rl.environments.buying;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.agent.AgentACDCSafe;
import safe_rl.domain.agent.value_objects.AgentParameters;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.trainer.TrainerOneStepACDC;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.environments.buying_electricity.*;
import safe_rl.environments.factories.FactoryOptModel;
import safe_rl.domain.trainer.helpers.EpisodeInfo;

/***
 * Optimal behavior in this test is max power two first steps
 critical params:  targetMean(2d).targetStd(3d).targetCritic(0d)
 */

public class TestTrainerOneStepACDCBuyer {

    public static final double SOC_START = 0.2;
    public static final double TOL_POWER = 1.1;
    public static final double SOC_END = 1.0;

    SettingsBuying settings3hours;
    TrainerOneStepACDC<VariablesBuying> trainer;

    @BeforeEach
    void init() {
        settings3hours = SettingsBuying.new3HoursSamePrice();
        var environment = new EnvironmentBuying(settings3hours);
        var startState = StateBuying.of(VariablesBuying.newSoc(SOC_START));
        var safetyLayer = new SafetyLayer<>(FactoryOptModel.createChargeModel(settings3hours));
        TrainerParameters trainerParams = TrainerParameters.newDefault()
                .withLearningRateReplayBufferActor(1e-3)  //1e-3
                .withLearningRateReplayBufferActorStd(1e-3)  //1e-3
                .withLearningRateReplayBufferCritic(1e-1)  //1e-1
                .withNofEpisodes(3000).withGamma(1.0).withRatioPenCorrectedAction(2d)
                ;
        var agent= AgentACDCSafe.<VariablesBuying>builder()
                .settings(settings3hours)
                .parameters(AgentParameters.newDefault()
                        .withTargetMean(2d).withTargetLogStd(Math.log(3d)).withTargetCritic(0d)
                                .withLearningRateActorMean(1e-3)
                                .withLearningRateActorStd(1e-3)
                                .withLearningRateCritic(1e-1)
                )
                .state(startState)
                .build();
        trainer= TrainerOneStepACDC.<VariablesBuying>builder()
                .environment(environment).agent(agent)
                .safetyLayer(safetyLayer)
                .parameters(trainerParams)
                .startStateSupplier(() -> startState.copy() )
                .build();
    }

    @Test
    @SneakyThrows
    void whenTrained_thenCorrect() {

        trainer.train();
        var experiences = trainer.getExperiences();

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
        Assertions.assertEquals(powerExpected,va0, TOL_POWER);

    }



}
