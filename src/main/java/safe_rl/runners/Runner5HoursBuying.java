package safe_rl.runners;

import org.apache.commons.math3.util.Pair;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.trainers.TrainerMultiStepACDC;
import safe_rl.domain.value_classes.SimulationResult;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.*;
import safe_rl.helpers.AgentSimulator;

import java.util.List;

/**
 * small withStepHorizon => bad convergence
 * learning rates very critical
 */

public class Runner5HoursBuying {

    static StateI<VariablesBuying> startState;

    public static final double SOC_START = 0.0;
    public static void main(String[] args) {
        var trainerAndSimulator = createTrainerAndSimulator();
        var trainer=trainerAndSimulator.getFirst();
        trainer.train();
        trainer.getRecorder().recorderTrainingProgress.plot("Multi step ACDC");
        var simulator=trainerAndSimulator.getSecond();
        var simRes=simulator.simulateWithNoExploration();
        printing(trainer, simRes);
    }

    private static void printing(
            TrainerMultiStepACDC<VariablesBuying> trainer, List<SimulationResult<VariablesBuying>> simRes) {
        System.out.println("agent = " + trainer.getAgent());
        SimulationResult.sumRewards(simRes);
        SimulationResult.print(simRes);
    }

    private static Pair<
            TrainerMultiStepACDC<VariablesBuying>
            , AgentSimulator<VariablesBuying>> createTrainerAndSimulator() {
        var settings5 = BuySettings.new5HoursDecreasingPrice();
        var environment = new EnvironmentBuying(settings5);
        startState = StateBuying.of(VariablesBuying.newSoc(SOC_START));
        var safetyLayer = new SafetyLayerBuying<VariablesBuying>(settings5);
        var agent=AgentACDCSafeBuyer.builder()
                .settings(settings5)
                .targetMean(2d).targetLogStd(Math.log(5d)).targetCritic(0d)
                .learningRateActorMean(1e-3).learningRateActorStd(1e-4).learningRateCritic(1e-2)
                .deltaThetaMax(10d)
                .state((StateBuying) startState.copy())
                .build();
        var trainerParameters= TrainerParameters.newDefault()
                .withNofEpisodes(5000).withGamma(1.0).withRatioPenCorrectedAction(10d).withStepHorizon(4);
        TrainerMultiStepACDC<VariablesBuying> trainer = TrainerMultiStepACDC.<VariablesBuying>builder()
                .environment(environment).agent(agent)
                .safetyLayer(safetyLayer)
                .trainerParameters(trainerParameters)
                .startStateSupplier(() -> startState.copy())
                .build();
        var simulator= AgentSimulator.<VariablesBuying>builder()
                .agent(agent).safetyLayer(safetyLayer).startStateSupplier(() -> startState.copy())
                .environment(environment).build();

        return Pair.create(trainer,simulator);
    }

}
