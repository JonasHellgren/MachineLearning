package safe_rl.runners;

import lombok.extern.java.Log;
import safe_rl.domain.trainers.TrainerOneStepACDC;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.*;
import safe_rl.environments.factories.FactoryOptModel;

/***
 * targetLogStd: very important, good enough init exploration needed
 */

@Log
public class Runner3HoursBuying {
    public static final double SOC_START = 0.2;
    public static void main(String[] args) {
        var trainer = createTrainer();
        trainer.train();
        trainer.getRecorder().recorderTrainingProgress.plot("One step ACDC");
        log.info("agent = " + trainer.getAgent());
    }

    private static TrainerOneStepACDC<VariablesBuying> createTrainer() {
        var settings3 = SettingsBuying.new3HoursSamePrice();
        var environment = new EnvironmentBuying(settings3);
        var startState = StateBuying.of(VariablesBuying.newSoc(SOC_START));
        var safetyLayer = new SafetyLayer<VariablesBuying>(FactoryOptModel.createChargeModel(settings3));
        var agent=AgentACDCSafeBuyer.builder()
                .settings(settings3)
                .targetMean(2d).targetLogStd(Math.log(3d)).targetCritic(0d)
                .learningRateActorMean(1e-2).learningRateActorStd(1e-3).learningRateCritic(1e-1)
                .gradMax(10d)
                .state(startState)
                .build();
        var trainerParameters= TrainerParameters.newDefault()
                .withNofEpisodes(2000).withGamma(1.0).withRatioPenCorrectedAction(10d);
        return TrainerOneStepACDC.<VariablesBuying>builder()
                .environment(environment).agent(agent)
                .safetyLayer(safetyLayer)
                .trainerParameters(trainerParameters)
                .startStateSupplier(() -> startState.copy() )
                .build();
    }

}
