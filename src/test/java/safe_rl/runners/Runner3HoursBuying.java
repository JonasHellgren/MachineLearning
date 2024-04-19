package safe_rl.runners;

import safe_rl.domain.trainers.TrainerOneStepACDC;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.*;

public class Runner3HoursBuying {
    public static final double SOC_START = 0.2;
    public static void main(String[] args) {
        var trainer = createTrainer();
        trainer.train();
        trainer.recorders.recorderTrainingProgress.plot("One step ACDC");
    }

    private static TrainerOneStepACDC<VariablesBuying> createTrainer() {
        var settings3 = BuySettings.new3HoursSamePrice();
        var environment = new EnvironmentBuying(settings3);
        var startState = StateBuying.of(VariablesBuying.newSoc(SOC_START));
        var safetyLayer = new SafetyLayerBuying<VariablesBuying>(settings3);
        var agent=AgentACDCSafeBuyer.builder()
                .settings(settings3)
                .targetMean(2d).targetLogStd(Math.log(1d)).targetCritic(0d)
                .learningRateActorMean(1e-2).learningRateActorStd(1e-3).learningRateCritic(1e-1)
                .state(startState)
                .build();
        var trainerParameters= TrainerParameters.newDefault()
                .withNofEpisodes(2000).withGamma(1.0);
        return TrainerOneStepACDC.<VariablesBuying>builder()
                .environment(environment).agent(agent)
                .safetyLayer(safetyLayer)
                .trainerParameters(trainerParameters)
                .startState(startState)
                .build();
    }

}
