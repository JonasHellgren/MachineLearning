package safe_rl.runners.trading;

import com.google.common.collect.Range;
import com.joptimizer.exception.JOptimizerException;
import common.list_arrays.ListUtils;
import common.other.CpuTimer;
import lombok.Builder;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.agent.AgentACDCSafe;
import safe_rl.domain.agent.aggregates.DisCoMemory;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.environment.EnvironmentI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.trainer.TrainerMultiStepACDC;
import safe_rl.domain.simulator.value_objects.SimulationResult;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.environments.factories.AgentParametersFactory;
import safe_rl.environments.factories.FactoryOptModel;
import safe_rl.environments.factories.SettingsTradingFactory;
import safe_rl.environments.factories.TrainerParametersFactory;
import safe_rl.environments.trading_electricity.*;
import safe_rl.domain.simulator.AgentSimulator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Builder
@Log
public class RunnerHelperTrading<V> {

    SettingsTrading settings;
    int nSim;
    double socStart;

    public  static final String PICS="src/main/java/safe_rl/runners/pics";


    public  Pair<TrainerMultiStepACDC<V>, AgentSimulator<V>> createTrainerAndSimulator(
            TrainerParameters trainerParameters) {
        EnvironmentI<V> environment = (EnvironmentI<V>) new EnvironmentTrading(settings);
        StateI<V>  startState = (StateI<V>) StateTrading.of(VariablesTrading.newSoc(socStart));
        SafetyLayer<V> safetyLayer = (SafetyLayer<V>) new SafetyLayer<>(FactoryOptModel.createTradeModel(settings));

        AgentACDiscoI<V> agent = AgentACDCSafe.of(
                AgentParametersFactory.trading24Hours(
                        settings,
                        settings.powerCapacityFcrRange().upperEndpoint()),
                settings,
                startState.copy());
        var trainer = TrainerMultiStepACDC.<V>builder()
                .environment(environment).agent(agent)
                .safetyLayer(safetyLayer)
                .trainerParameters(trainerParameters)
                .startStateSupplier(() -> startState.copy())
                .build();
        var simulator = AgentSimulator.<V>builder()
                .agent(agent).safetyLayer(safetyLayer)
                .startStateSupplier(() -> startState.copy())
                .environment(environment).build();

        return Pair.create(trainer,simulator);
    }

    public static Pair<TrainerMultiStepACDC<VariablesTrading>, AgentSimulator<VariablesTrading>> getAgentSimulatorPair(
            SettingsTrading settings, int nSimulations, double socStart) {
        var helper = RunnerHelperTrading.<VariablesTrading>builder()
                .nSim(nSimulations)
                .settings(settings)
                .socStart(socStart)
                .build();
        return helper.createTrainerAndSimulator(TrainerParametersFactory.tradingNightHoursFewEpisodes());
    }

    public static SettingsTrading getSettings(Pair<List<Double>,List<Double>> energyFcrPricePair,
                                              double cap,
                                              double socTerminalMin) {
        return SettingsTradingFactory.new100kWhVehicleEmptyPrices()
                .withPowerCapacityFcrRange(Range.closed(0d, cap))
                .withEnergyPriceTraj(ListUtils.toArray(energyFcrPricePair.getFirst()))
                .withCapacityPriceTraj(ListUtils.toArray(energyFcrPricePair.getSecond()))
                .withSocTerminalMin(socTerminalMin);
    }

    public  void plotAndPrint(
            Pair<TrainerMultiStepACDC<VariablesTrading>,
                    AgentSimulator<VariablesTrading>> trainerAndSimulator,
            CpuTimer timer, String title) throws JOptimizerException {
        var trainer=trainerAndSimulator.getFirst();
        var recorder = trainer.getRecorder();
        recorder.plot("Multi step ACDC trading");
        recorder.saveCharts(RunnerHelperTrading.PICS);
        var helper = RunnerHelperTrading.builder().nSim(nSim).settings(settings).build();
        helper.printing(trainer, timer);
        var simulator = trainerAndSimulator.getSecond();
        helper.simulateAndPlot(simulator);
        helper.simulateAndSavePlots(simulator, title);
        helper.plotMemory(trainer.getAgent().getCritic(), "critic");
        helper.plotMemory(trainer.getAgent().getActorMean(), "actor mean");
    }

    void simulateAndPlot(AgentSimulator<VariablesTrading> simulator) throws JOptimizerException {
        var simulationResultsMap =  getSimulationResultsMap(simulator);
        double valueInStartState=simulator.valueInStartState();
        new TradeSimulationPlotter<VariablesTrading>(settings).plot(simulationResultsMap,valueInStartState);
    }

    void simulateAndSavePlots(AgentSimulator<VariablesTrading> simulator,
                                     String caseName) throws JOptimizerException {
        var simulationResultsMap =  getSimulationResultsMap(simulator);
        new TradeSimulationPlotter<VariablesTrading>(settings).savePlots(simulationResultsMap,PICS,caseName);
    }

    Map<Integer, List<SimulationResult<VariablesTrading>>> getSimulationResultsMap(
            AgentSimulator<VariablesTrading> simulator) throws JOptimizerException {
        Map<Integer, List<SimulationResult<VariablesTrading>>> simulationResultsMap=new HashMap<>();
        for (int i = 0; i < nSim; i++) {
            simulationResultsMap.put(i, simulator.simulateWithNoExploration());
        }
        return simulationResultsMap;
    }

    void plotMemory(DisCoMemory<VariablesTrading> critic, String name) {
        TradingMemoryPlotter<VariablesTrading> plotter=
                new TradingMemoryPlotter<>(critic, name,(int) settings.timeEnd());
        plotter.plot();
    }

      void printing(
            TrainerMultiStepACDC<VariablesTrading> trainer,
            CpuTimer timer) {
        log.info("agent = " + trainer.getAgent());
        timer.stop();
        log.info("timer (ms) = " + timer.getAbsoluteProgress());
    }

}
