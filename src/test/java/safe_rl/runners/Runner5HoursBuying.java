package safe_rl.runners;

import safe_rl.domain.trainers.TrainerMultiStepACDC;
import safe_rl.domain.trainers.TrainerOneStepACDC;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.*;

public class Runner5HoursBuying {

    public static final double SOC_START = 0.0;
    public static void main(String[] args) {
        var trainer = createTrainer();
        trainer.train();
        trainer.recorders.recorderTrainingProgress.plot("One step ACDC");
    }

    private static TrainerMultiStepACDC<VariablesBuying> createTrainer() {
        var settings5 = BuySettings.new5HoursIncreasingPrice();
        var environment = new EnvironmentBuying(settings5);
        var startState = StateBuying.of(VariablesBuying.newSoc(SOC_START));
        var safetyLayer = new SafetyLayerBuying<VariablesBuying>(settings5);
        var agent=AgentACDCSafeBuyer.builder()
                .settings(settings5)
                .targetMean(2d).targetLogStd(Math.log(3d)).targetCritic(0d)
                .learningRateActorMean(1e-2).learningRateActorStd(1e-3).learningRateCritic(1e-1)
                .deltaThetaMax(10d)
                .state(startState)
                .build();
        var trainerParameters= TrainerParameters.newDefault()
                .withNofEpisodes(2000).withGamma(1.0).withRatioPenCorrectedAction(2d).withStepHorizon(3);
        return TrainerMultiStepACDC.<VariablesBuying>builder()
                .environment(environment).agent(agent)
                .safetyLayer(safetyLayer)
                .trainerParameters(trainerParameters)
                .startState(startState)
                .build();
    }

}
