package safe_rl.runners;

import common.other.CpuTimer;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.trainers.TrainerMultiStepACDC;
import safe_rl.domain.value_classes.SimulationResult;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.*;
import safe_rl.environments.factories.FactoryOptModel;
import safe_rl.environments.trading_electricity.EnvironmentTrading;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.helpers.AgentSimulator;
import java.util.List;

@Log
public class Runner5HoursTrading {

    static StateI<VariablesTrading> startState;

    public static final double SOC_START = 0.5;
    public static void main(String[] args) {
        var trainerAndSimulator = createTrainerAndSimulator();
        var trainer=trainerAndSimulator.getFirst();
        var timer= CpuTimer.newWithTimeBudgetInMilliSec(0);
        trainer.train();
        trainer.getRecorder().recorderTrainingProgress.plot("Multi step ACDC");
        var simulator=trainerAndSimulator.getSecond();
        var simRes=simulator.simulateWithNoExploration();
        printing(trainer, simRes,timer);
    }

    private static void printing(
            TrainerMultiStepACDC<VariablesTrading> trainer,
            List<SimulationResult<VariablesTrading>> simRes,
            CpuTimer timer) {
        log.info("agent = " + trainer.getAgent());
        SimulationResult.sumRewards(simRes);
        SimulationResult.print(simRes);
        timer.stop();
        log.info("timer (ms) = " + timer.getAbsoluteProgress());

    }

    private static Pair<
            TrainerMultiStepACDC<VariablesTrading>
            , AgentSimulator<VariablesTrading>> createTrainerAndSimulator() {
        //interesting to change, decreasing vs increasing price

        var settings5 = SettingsTrading.new5HoursIncreasingPrice()
                .withPriceFCR(0).withSocTerminalMin(SOC_START).withPriceBattery(0e3);
        var environment = new EnvironmentTrading(settings5);
        startState = StateTrading.of(VariablesTrading.newSoc(SOC_START));
        var safetyLayer = new SafetyLayer<>(FactoryOptModel.createTradeModel(settings5));
        var agent= AgentACDCSafe.<VariablesTrading>builder()
                .settings(settings5)
                .targetMean(0.0d).targetLogStd(Math.log(5d)).targetCritic(0d).absActionNominal(1d)
                .learningRateActorMean(1e-3).learningRateActorStd(1e-4).learningRateCritic(1e-2)
                .gradMax(1d)
                .state(startState.copy())
                .build();
        var trainerParameters= TrainerParameters.newDefault()
                .withNofEpisodes(15000).withGamma(0.99).withRatioPenCorrectedAction(0.1d).withStepHorizon(3);
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
