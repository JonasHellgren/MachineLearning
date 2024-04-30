package safe_rl.runners;

import com.joptimizer.exception.JOptimizerException;
import common.other.CpuTimer;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.agents.AgentACDCSafe;
import safe_rl.domain.memories.DisCoMemory;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.trainers.TrainerMultiStepACDC;
import safe_rl.domain.value_classes.SimulationResult;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.factories.FactoryOptModel;
import safe_rl.environments.trading_electricity.*;
import safe_rl.helpers.AgentSimulator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/***
 * withLearningRateReplayBufferActor(1e-2).withGradMeanActorMaxBufferFitting needs to be small
 */

@Log
public class Runner5HoursTrading {

    public static final double PRICE_BATTERY = 1e3;
    public static final int N_SIMULATIONS = 5;
    static StateI<VariablesTrading> startState;
    static SettingsTrading settings5;

    public static final double SOC_START = 0.5;
    public static final double SOC_INCREASE = 0.0;

    @SneakyThrows
    public static void main(String[] args) {
        var trainerAndSimulator = createTrainerAndSimulator();
        var trainer=trainerAndSimulator.getFirst();
        var timer= CpuTimer.newWithTimeBudgetInMilliSec(0);
        trainer.train();
        trainer.getRecorder().recorderTrainingProgress.plot("Multi step ACDC trading");
        printing(trainer, timer);
        simulateAndPlot(trainerAndSimulator.getSecond());
        plotMemory(trainer.getAgent().getCritic(), "critic");
        plotMemory(trainer.getAgent().getActorMean(), "actor mean");
    }

    private static void simulateAndPlot(AgentSimulator<VariablesTrading> simulator) throws JOptimizerException {
        Map<Integer, List<SimulationResult<VariablesTrading>>> simulationResultsMap=new HashMap<>();
        for (int i = 0; i < N_SIMULATIONS; i++) {
            simulationResultsMap.put(i, simulator.simulateWithNoExploration());
        }
        new TradeSimulationPlotter<VariablesTrading>(settings5).plot(simulationResultsMap);
    }

    private static void plotMemory(DisCoMemory<VariablesTrading> critic, String name) {
        TradingMemoryPlotter<VariablesTrading> plotter=
                new TradingMemoryPlotter<>(critic, name,(int) settings5.timeEnd());
        plotter.plot();
    }

    private static void printing(
            TrainerMultiStepACDC<VariablesTrading> trainer,
            CpuTimer timer) {
        log.info("agent = " + trainer.getAgent());
        timer.stop();
        log.info("timer (ms) = " + timer.getAbsoluteProgress());
    }

    private static Pair<
            TrainerMultiStepACDC<VariablesTrading>
            , AgentSimulator<VariablesTrading>> createTrainerAndSimulator() {
        //interesting to change, decreasing vs increasing price

        settings5 = SettingsTrading.new5HoursIncreasingPrice()
                .withPowerCapacityFcr(1.0).withPriceFCR(0.1).withStdActivationFCR(0.1)
                .withSocTerminalMin(SOC_START+ SOC_INCREASE).withPriceBattery(PRICE_BATTERY);
        var environment = new EnvironmentTrading(settings5);
        startState = StateTrading.of(VariablesTrading.newSoc(SOC_START));
        var safetyLayer = new SafetyLayer<>(FactoryOptModel.createTradeModel(settings5));
        var agent= AgentACDCSafe.<VariablesTrading>builder()
                .settings(settings5)
                .targetMean(0.0d).targetLogStd(Math.log(3d)).targetCritic(0d).absActionNominal(1d)
                .learningRateActorMean(1e-2).learningRateActorStd(1e-2).learningRateCritic(1e-3)
                .gradMaxActor0(1d).gradMaxCritic0(1d)
                .state(startState.copy())
                .build();
        var trainerParameters= TrainerParameters.newDefault()
                .withNofEpisodes(3000).withGamma(0.99).withRatioPenCorrectedAction(0.1d).withStepHorizon(3)
                .withLearningRateReplayBufferCritic(1e-1)
                .withLearningRateReplayBufferActor(1e-2).withGradMeanActorMaxBufferFitting(1e-3)
                .withReplayBufferSize(1000).withMiniBatchSize(100).withNReplayBufferFitsPerEpisode(2);
        var trainer = TrainerMultiStepACDC.<VariablesTrading>builder()
                .environment(environment).agent(agent)
                .safetyLayer(safetyLayer)
                .trainerParameters(trainerParameters)
                .startStateSupplier(() -> startState.copy())
                .build();
        var simulator= AgentSimulator.<VariablesTrading>builder()
                .agent(agent).safetyLayer(safetyLayer)
                .startStateSupplier(() -> startState.copy())
                .environment(environment).build();

        return Pair.create(trainer,simulator);
    }
    
}
