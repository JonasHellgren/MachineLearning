package safe_rl.environments.buying;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.trainers.TrainerOneStepACDC;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.*;

public class TestTrainerACDCBuyer {

    public static final double SOC = 0.2;
    public static final double TARGET_MEAN = 1d;
    public static final double TARGET_STD = 0.5;
    public static final double ADV = 1d;
    public static final double TARGET_CRITIC = 0d;
    public static final double TOL_POWER = 1e-1;
    public static final int NOF_EPISODES = 10;

    TrainerOneStepACDC<VariablesBuying> trainer;

    @BeforeEach
    void init() {
        var settings3 = BuySettings.new3HoursSamePrice();
        var environment = new EnvironmentBuying(settings3);
        var startState = StateBuying.of(VariablesBuying.newSoc(SOC));
        var safetyLayer = new SafetyLayerBuying<VariablesBuying>(settings3);
        var agent=AgentACDCSafeBuyer.builder()
                .settings(BuySettings.new5HoursIncreasingPrice())
                .targetMean(TARGET_MEAN).targetStd(TARGET_STD).targetCritic(TARGET_CRITIC)
                .state(startState)
                .build();
        var trainerParameters=TrainerParameters.newDefault().withNofEpisodes(NOF_EPISODES);
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



    }



}
