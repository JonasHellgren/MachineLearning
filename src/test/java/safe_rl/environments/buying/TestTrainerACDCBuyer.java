package safe_rl.environments.buying;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.trainers.TrainerOneStepACDC;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.*;
import safe_rl.helpers.EpisodeInfo;

public class TestTrainerACDCBuyer {

    public static final double SOC = 0.2;
    public static final double TARGET_MEAN = 2d;
    public static final double TARGET_STD = 3;
    public static final double ADV = 1d;
    public static final double TARGET_CRITIC = 0d;
    public static final double TOL_POWER = 1e-1;
    public static final int NOF_EPISODES = 500;

    BuySettings settings3;
    TrainerOneStepACDC<VariablesBuying> trainer;

    @BeforeEach
    void init() {
        settings3 = BuySettings.new3HoursSamePrice();
        var environment = new EnvironmentBuying(settings3);
        var startState = StateBuying.of(VariablesBuying.newSoc(SOC));
        var safetyLayer = new SafetyLayerBuying<VariablesBuying>(settings3);
        var agent=AgentACDCSafeBuyer.builder()
                .settings(settings3)
                .targetMean(TARGET_MEAN).targetStd(TARGET_STD).targetCritic(TARGET_CRITIC)
                .learningRateActorMean(1e-2).learningRateActorStd(0e-1).learningRateCritic(1e-1)
                .state(startState)
                .build();
        var trainerParameters=TrainerParameters.newDefault().withNofEpisodes(NOF_EPISODES).withGamma(1.0);
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
        var agent=trainer.getAgent();

        var experiences = trainer.evaluate();

        var ei=new EpisodeInfo<>(experiences);

        var powerList=ei.actionsApplied();

        System.out.println("powerList = " + powerList);

        Assertions.assertEquals(settings3.powerBattMax(),powerList.get(0),1e-3);

    }



}
