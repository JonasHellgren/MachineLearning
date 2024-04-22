package safe_rl.runners;

import org.apache.commons.math3.util.Pair;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.trainers.TrainerMultiStepACDC;
import safe_rl.domain.value_classes.SimulationResult;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.*;
import safe_rl.helpers.AgentActionSimulator;

public class Runner5HoursBuying {

    static StateI<VariablesBuying> startState;

    public static final double SOC_START = 0.0;
    public static void main(String[] args) {
        var trainerAndSimulator = createTrainerAndSimulator();
        var trainer=trainerAndSimulator.getFirst();
        trainer.train();
        trainer.recorders.recorderTrainingProgress.plot("Multi step ACDC");
        var simulator=trainerAndSimulator.getSecond();
        var simRes=simulator.simulate(startState.copy());
        System.out.println("startState = " + startState);
        double sumRew= SimulationResult.sumRewards(simRes);
        simRes.forEach(System.out::println);
        System.out.println("sumRew = " + sumRew);


    }

    private static Pair<
            TrainerMultiStepACDC<VariablesBuying>
            ,AgentActionSimulator<VariablesBuying>> createTrainerAndSimulator() {
        var settings5 = BuySettings.new5HoursIncreasingPrice();
        var environment = new EnvironmentBuying(settings5);
        startState = StateBuying.of(VariablesBuying.newSoc(SOC_START));
        var safetyLayer = new SafetyLayerBuying<VariablesBuying>(settings5);
        var agent=AgentACDCSafeBuyer.builder()
                .settings(settings5)
                .targetMean(2d).targetLogStd(Math.log(3d)).targetCritic(0d)
                .learningRateActorMean(1e-2).learningRateActorStd(1e-4).learningRateCritic(1e-1)
                .deltaThetaMax(10d)
                .state((StateBuying) startState.copy())
                .build();
        var trainerParameters= TrainerParameters.newDefault()
                .withNofEpisodes(2000).withGamma(1.0).withRatioPenCorrectedAction(2d).withStepHorizon(3);
        TrainerMultiStepACDC<VariablesBuying> trainer = TrainerMultiStepACDC.<VariablesBuying>builder()
                .environment(environment).agent(agent)
                .safetyLayer(safetyLayer)
                .trainerParameters(trainerParameters)
                .startState(startState.copy())
                .build();
        var simulator= AgentActionSimulator.<VariablesBuying>builder()
                .agent(agent).safetyLayer(safetyLayer).settings(settings5)
                .environment(environment).build();

        return Pair.create(trainer,simulator);
    }

}
