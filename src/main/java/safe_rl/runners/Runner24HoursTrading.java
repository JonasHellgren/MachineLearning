package safe_rl.runners;

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
        var trainer=trainerAndSimulator.getFirst();
        var timer= CpuTimer.newWithTimeBudgetInMilliSec(0);
        var helper= RunnerHelper.builder().nSim(N_SIMULATIONS).settings(settings5).build();
        trainer.train();
        trainer.getRecorder().recorderTrainingProgress.plot("Multi step ACDC trading");
        helper.printing(trainer, timer);
        helper.simulateAndPlot(trainerAndSimulator.getSecond());
        helper.plotMemory(trainer.getAgent().getCritic(), "critic");
        helper.plotMemory(trainer.getAgent().getActorMean(), "actor mean");
    }

    private static Pair<
            TrainerMultiStepACDC<VariablesTrading>
            , AgentSimulator<VariablesTrading>> createTrainerAndSimulator() {
        //interesting to change, decreasing vs increasing price

        settings5 = SettingsTrading.new24HoursIncreasingPrice()
                .withPowerCapacityFcr(POWER_CAPACITY_FCR).withStdActivationFCR(0.1)
                .withSocTerminalMin(SOC_START+ SOC_INCREASE).withPriceBattery(PRICE_BATTERY);


        System.out.println("settings5.priceTraj() = " + Arrays.toString(settings5.priceTraj()));

        var environment = new EnvironmentTrading(settings5);
        startState = StateTrading.of(VariablesTrading.newSoc(SOC_START));
        var safetyLayer = new SafetyLayer<>(FactoryOptModel.createTradeModel(settings5));
        double powerNom=settings5.powerBattMax()/10;
        var agent= AgentACDCSafe.<VariablesTrading>builder()
                .settings(settings5)
                .targetMean(0.0d).targetLogStd(Math.log(settings5.powerBattMax()))
                .targetCritic(0d).absActionNominal(powerNom)
                .learningRateActorMean(1e-2).learningRateActorStd(1e-3).learningRateCritic(1e-2)
                //.learningRateActorMean(1e-3).learningRateActorStd(1e-2).learningRateCritic(1e-3)
                .gradMaxActor0(1d).gradMaxCritic0(POWER_CAPACITY_FCR)
                .state(startState.copy())
                .build();
        var trainerParameters= TrainerParameters.newDefault()
                .withNofEpisodes(3000).withGamma(1.00).withStepHorizon(10)
                .withLearningRateReplayBufferActor(1e-1)
                .withLearningRateReplayBufferActorStd(1e-2)
                .withLearningRateReplayBufferCritic(1e-1)
                .withGradMeanActorMaxBufferFitting(1d)
                .withReplayBufferSize(1000).withMiniBatchSize(50).withNReplayBufferFitsPerEpisode(3);
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
