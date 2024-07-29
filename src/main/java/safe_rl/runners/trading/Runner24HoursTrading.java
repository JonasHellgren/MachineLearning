package safe_rl.runners.trading;

import com.joptimizer.exception.JOptimizerException;
import common.other.CpuTimer;
import lombok.SneakyThrows;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.agent.value_objects.AgentParameters;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.agent.AgentACDCSafe;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.trainer.TrainerMultiStepACDC;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.environments.factories.AgentParametersFactory;
import safe_rl.environments.factories.FactoryOptModel;
import safe_rl.environments.factories.SettingsTradingFactory;
import safe_rl.environments.factories.TrainerParametersFactory;
import safe_rl.environments.trading_electricity.EnvironmentTrading;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.domain.simulator.AgentSimulator;

import java.util.List;

public class Runner24HoursTrading {
    public static final double PRICE_BATTERY = 30e3;

    public static int CASE_NR = 0;
    public static List<String> CASES = List.of("zeroCap90Tar", "30Cap50Tar", "zigZaw");
    public static final List<Double> POWER_CAPACITY_FCR_LIST = List.of(0d, 30.0, 10.0);
    public static final List<Double> SOC_INCREASE_LSIT = List.of(0.4, 0.0, 0.0);

    public static final int N_SIMULATIONS = 5;
    static StateI<VariablesTrading> startState;
    static SettingsTrading settings5;
    public static final double SOC_START = 0.5;

    @SneakyThrows
    public static void main(String[] args) {
        var trainerAndSimulator = createTrainerAndSimulator();
        var trainer = trainerAndSimulator.getFirst();
        var timer = CpuTimer.newWithTimeBudgetInMilliSec(0);
        trainer.train();
        plotAndPrint(trainerAndSimulator, trainer, timer);
    }

    private static Pair<
            TrainerMultiStepACDC<VariablesTrading>
            , AgentSimulator<VariablesTrading>> createTrainerAndSimulator() {
        //settings5 = SettingsTrading.new24HoursZigSawPrice()  //case 2
        Double gradCriticMax = POWER_CAPACITY_FCR_LIST.get(CASE_NR);
        settings5 = SettingsTradingFactory.new24HoursIncreasingPrice()  //case 0 and 1
                .withPowerCapacityFcr(gradCriticMax).withStdActivationFCR(0.1)
                .withSocTerminalMin(SOC_START + SOC_INCREASE_LSIT.get(CASE_NR)).withPriceBattery(PRICE_BATTERY);

        var environment = new EnvironmentTrading(settings5);
        startState = StateTrading.of(VariablesTrading.newSoc(SOC_START));
        var safetyLayer = new SafetyLayer<>(FactoryOptModel.createTradeModel(settings5));

        var agent = AgentACDCSafe.of(
                AgentParametersFactory.trading24Hours(settings5, gradCriticMax),
                settings5,
                startState.copy());
        var trainer = TrainerMultiStepACDC.<VariablesTrading>builder()
                .environment(environment).agent(agent)
                .safetyLayer(safetyLayer)
                .trainerParameters(TrainerParametersFactory.trading24Hours())
                .startStateSupplier(() -> startState.copy())
                .build();
        var simulator = AgentSimulator.<VariablesTrading>builder()
                .agent(agent).safetyLayer(safetyLayer)
                .startStateSupplier(() -> startState.copy())
                .environment(environment).build();

        return Pair.create(trainer, simulator);
    }


    private static void plotAndPrint(
            Pair<TrainerMultiStepACDC<VariablesTrading>,
                    AgentSimulator<VariablesTrading>> trainerAndSimulator,
            TrainerMultiStepACDC<VariablesTrading> trainer,
            CpuTimer timer) throws JOptimizerException {
        var trainingProgress = trainer.getRecorder().recorderTrainingProgress;
        trainingProgress.plot("Multi step ACDC trading");
        trainingProgress.saveCharts(RunnerHelper.PICS);
        var helper = RunnerHelper.builder().nSim(N_SIMULATIONS).settings(settings5).build();
        helper.printing(trainer, timer);
        var simulator = trainerAndSimulator.getSecond();
        helper.simulateAndPlot(simulator);
        helper.simulateAndSavePlots(simulator, CASES.get(CASE_NR));
        helper.plotMemory(trainer.getAgent().getCritic(), "critic");
        helper.plotMemory(trainer.getAgent().getActorMean(), "actor mean");
    }
}
