package safe_rl.runners;

import common.other.CpuTimer;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.agents.AgentACDCSafe;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.trainers.TrainerMultiStepACDC;
import safe_rl.domain.value_classes.SimulationResult;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.*;
import safe_rl.environments.factories.FactoryOptModel;
import safe_rl.helpers.AgentSimulator;

import java.util.List;

/**
 * high withStepHorizon => better convergence
 * learning rates and gradMax very critical
 * gamma<1 seems to improve convergence

 * BuySettings: decreasing price shall give high power in end, increasing price high power in start
 */

@Log
public class Runner5HoursBuying {

    static StateI<VariablesBuying> startState;

    public static final double SOC_START = 0.0;
    @SneakyThrows
    public static void main(String[] args) {
        var trainerAndSimulator = createTrainerAndSimulator();
        var trainer=trainerAndSimulator.getFirst();
        var timer=CpuTimer.newWithTimeBudgetInMilliSec(0);
        trainer.train();
        trainer.getRecorder().recorderTrainingProgress.plot("Multi step ACDC");
        var simulator=trainerAndSimulator.getSecond();
        var simRes=simulator.simulateWithNoExploration();
        printing(trainer, simRes,timer);
    }

    private static void printing(
            TrainerMultiStepACDC<VariablesBuying> trainer,
            List<SimulationResult<VariablesBuying>> simRes,
            CpuTimer timer) {
        log.info("agent = " + trainer.getAgent());
        SimulationResult.sumRewards(simRes);
        SimulationResult.print(simRes);
        timer.stop();
        log.info("timer (ms) = " + timer.getAbsoluteProgress());
    }

    private static Pair<
            TrainerMultiStepACDC<VariablesBuying>
            , AgentSimulator<VariablesBuying>> createTrainerAndSimulator() {
        var settings5 = SettingsBuying.new5HoursDecreasingPrice();  //interesting to change, decreasing vs increasing price
        var environment = new EnvironmentBuying(settings5);
        startState = StateBuying.of(VariablesBuying.newSoc(SOC_START));
        var safetyLayer = new SafetyLayer<>(FactoryOptModel.createChargeModel(settings5));
        var agent= AgentACDCSafe.<VariablesBuying>builder()
                .settings(settings5)
                .targetMean(2d).targetLogStd(Math.log(3d)).targetCritic(0d)
                .learningRateActorMean(1e-2).learningRateActorStd(1e-3).learningRateCritic(1e-3)
                .gradMaxActor0(1d).gradMaxCritic0(1d)
                .state( startState.copy())
                .build();
        var trainerParameters = TrainerParameters.newDefault()
                .withNofEpisodes(5000).withGamma(1.00).withRatioPenCorrectedAction(0.1d).withStepHorizon(5)
                .withLearningRateReplayBufferCritic(1e-1)
                .withLearningRateReplayBufferActor(1e-4).withGradActorMax(1e-3);
       var trainer = TrainerMultiStepACDC.<VariablesBuying>builder()
                .environment(environment).agent(agent)
                .safetyLayer(safetyLayer)
                .trainerParameters(trainerParameters)
                .startStateSupplier(() -> startState.copy())
                .build();
        var simulator= AgentSimulator.<VariablesBuying>builder()
                .agent(agent).safetyLayer(safetyLayer)
                .startStateSupplier(() -> startState.copy())
                .environment(environment).build();

        return Pair.create(trainer,simulator);
    }

}

/****
 * kasst med slump start
 *                 .startStateSupplier(() ->
 *                         StateBuying.of(VariablesBuying.newTimeSoc(
 *                                 RandUtils.getRandomIntNumber(0,(int) settings5.timeEnd()),
 *                                 RandUtils.randomNumberBetweenZeroAndOne())))
 */
