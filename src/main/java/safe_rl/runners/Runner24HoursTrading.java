package safe_rl.runners;

import com.joptimizer.exception.JOptimizerException;
import common.other.CpuTimer;
import lombok.SneakyThrows;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.agents.AgentACDCSafe;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.trainers.TrainerMultiStepACDC;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.factories.FactoryOptModel;
import safe_rl.environments.trading_electricity.EnvironmentTrading;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.helpers.AgentSimulator;

import java.util.Arrays;

public class Runner24HoursTrading {
    public static final double PRICE_BATTERY = 30e3;
    public static final double POWER_CAPACITY_FCR = 10.0;

    public static final int N_SIMULATIONS = 5;
    static StateI<VariablesTrading> startState;
    static SettingsTrading settings5;

    public static final double SOC_START = 0.5;
    public static final double SOC_INCREASE = 0.0;

    @SneakyThrows
    public static void main(String[] args) {
        var trainerAndSimulator = createTrainerAndSimulator();
        var trainer = trainerAndSimulator.getFirst();
        var timer = CpuTimer.newWithTimeBudgetInMilliSec(0);
        trainer.train();
        plotAndPrint(trainerAndSimulator, trainer, timer);
    }

    private static void plotAndPrint(Pair<TrainerMultiStepACDC<VariablesTrading>, AgentSimulator<VariablesTrading>> trainerAndSimulator, TrainerMultiStepACDC<VariablesTrading> trainer, CpuTimer timer) throws JOptimizerException {
        trainer.getRecorder().recorderTrainingProgress.plot("Multi step ACDC trading");
        var helper = RunnerHelper.builder().nSim(N_SIMULATIONS).settings(settings5).build();
        helper.printing(trainer, timer);
        helper.simulateAndPlot(trainerAndSimulator.getSecond());
        helper.plotMemory(trainer.getAgent().getCritic(), "critic");
        helper.plotMemory(trainer.getAgent().getActorMean(), "actor mean");
    }

    private static Pair<
            TrainerMultiStepACDC<VariablesTrading>
            , AgentSimulator<VariablesTrading>> createTrainerAndSimulator() {
        settings5 = SettingsTrading.new24HoursZigSawPrice()
                .withPowerCapacityFcr(POWER_CAPACITY_FCR).withStdActivationFCR(0.1)
                .withSocTerminalMin(SOC_START + SOC_INCREASE).withPriceBattery(PRICE_BATTERY);

        var environment = new EnvironmentTrading(settings5);
        startState = StateTrading.of(VariablesTrading.newSoc(SOC_START));
        var safetyLayer = new SafetyLayer<>(FactoryOptModel.createTradeModel(settings5));
        double powerNom = settings5.powerBattMax() / 10;

        var trainerParameters = TrainerParameters.builder()
                .nofEpisodes(8000).gamma(1.00).stepHorizon(10)
                .learningRateReplayBufferCritic(1e-1)
                .learningRateReplayBufferActor(1e-2)
                .learningRateReplayBufferActorStd(1e-3)
                .gradActorMax(1d).gradCriticMax(POWER_CAPACITY_FCR)
                .targetMean(0.0d).targetLogStd(Math.log(settings5.powerBattMax()))
                .targetCritic(0d).absActionNominal(powerNom)
                .replayBufferSize(1000).miniBatchSize(50).nReplayBufferFitsPerEpisode(5)
                .build();
        var agent = AgentACDCSafe.newFromTrainerParams(trainerParameters, settings5, startState.copy());
        var trainer = TrainerMultiStepACDC.<VariablesTrading>builder()
                .environment(environment).agent(agent)
                .safetyLayer(safetyLayer)
                .trainerParameters(trainerParameters)
                .startStateSupplier(() -> startState.copy())
                .build();
        var simulator = AgentSimulator.<VariablesTrading>builder()
                .agent(agent).safetyLayer(safetyLayer)
                .startStateSupplier(() -> startState.copy())
                .environment(environment).build();

        return Pair.create(trainer, simulator);
    }
}
