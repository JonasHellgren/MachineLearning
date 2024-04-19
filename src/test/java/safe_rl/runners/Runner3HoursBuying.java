package safe_rl.runners;

import com.google.common.collect.Table;
import org.junit.jupiter.api.BeforeEach;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;
import safe_rl.domain.trainers.TrainerOneStepACDC;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.*;

import java.util.List;

public class Runner3HoursBuying {

    public static final double SOC_START = 0.2;
    static final List<String> MEASURES_RECORDED = List.of("mean", "std", "value");


    public static void main(String[] args) {
        BuySettings settings3;
        TrainerOneStepACDC<VariablesBuying> trainer;
            settings3 = BuySettings.new3HoursSamePrice();
            var environment = new EnvironmentBuying(settings3);
            var startState = StateBuying.of(VariablesBuying.newSoc(SOC_START));
            var safetyLayer = new SafetyLayerBuying<VariablesBuying>(settings3);
            var agent=AgentACDCSafeBuyer.builder()
                    .settings(settings3)
                    .targetMean(2d).targetStd(3d).targetCritic(0d)
                    .learningRateActorMean(1e-2).learningRateActorStd(0e-1).learningRateCritic(1e-1)
                    .state(startState)
                    .build();
            var trainerParameters= TrainerParameters.newDefault()
                    .withNofEpisodes(300).withGamma(1.0);
            trainer= TrainerOneStepACDC.<VariablesBuying>builder()
                    .environment(environment).agent(agent)
                    .safetyLayer(safetyLayer)
                    .trainerParameters(trainerParameters)
                    .startState(startState)
                    .build();

        trainer.train();
        trainer.recorders.recorderTrainingProgress.plot("Neural CEM");

    }

}
