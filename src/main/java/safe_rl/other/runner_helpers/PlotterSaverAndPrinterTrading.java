package safe_rl.other.runner_helpers;

import com.joptimizer.exception.JOptimizerException;
import common.other.CpuTimer;
import lombok.Builder;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.agent.aggregates.DisCoMemory;
import safe_rl.domain.trainer.TrainerMultiStepACDC;
import safe_rl.domain.simulator.value_objects.SimulationResult;
import safe_rl.environments.trading_electricity.*;
import safe_rl.domain.simulator.AgentSimulator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static safe_rl.persistance.ElDataFinals.N_SIMULATIONS_PLOTTING;
import static safe_rl.persistance.ElDataFinals.PICS_FOLDER;


@Builder
@Log
public class PlotterSaverAndPrinterTrading<V> {

    SettingsTrading settings;
    int nSim;


    public  void plotAndPrint(
            Pair<TrainerMultiStepACDC<VariablesTrading>,
                    AgentSimulator<VariablesTrading>> trainerAndSimulator,
            CpuTimer timer, String title) throws JOptimizerException {
        var trainer=trainerAndSimulator.getFirst();
        var recorder = trainer.getRecorder();
        recorder.plot("Multi step ACDC trading");
     //   recorder.saveCharts(RunnerHelperTrading.PICS);
        var simulator = trainerAndSimulator.getSecond();
        simulateAndPlot(simulator);
        simulateAndSavePlots(simulator, title);
    }

    public void plotMemories(TrainerMultiStepACDC<VariablesTrading> trainer) {
        plotMemory(trainer.getAgent().getCritic(), "critic");
        plotMemory(trainer.getAgent().getActorMean(), "actor mean");
    }


    public static void plotting(SettingsTrading settings, Pair<TrainerMultiStepACDC<VariablesTrading>, AgentSimulator<VariablesTrading>> trainerAndSimulator) throws JOptimizerException {
        var helper = PlotterSaverAndPrinterTrading.<VariablesTrading>builder()
                .nSim(N_SIMULATIONS_PLOTTING).settings(settings)
                .build();
        helper.plotAndPrint(trainerAndSimulator,new CpuTimer(),"");
    }

    public void simulateAndPlot(AgentSimulator<VariablesTrading> simulator) throws JOptimizerException {
        var simulationResultsMap =  getSimulationResultsMap(simulator);
        double valueInStartState=simulator.criticValueInStartState();
        new TradeSimulationPlotter<VariablesTrading>(settings).plot(simulationResultsMap,valueInStartState);
    }

    public void simulateAndSavePlots(AgentSimulator<VariablesTrading> simulator,
                                     String caseName) throws JOptimizerException {
        var simulationResultsMap =  getSimulationResultsMap(simulator);
        new TradeSimulationPlotter<VariablesTrading>(settings).savePlots(simulationResultsMap, PICS_FOLDER,caseName);
    }

    Map<Integer, List<SimulationResult<VariablesTrading>>> getSimulationResultsMap(
            AgentSimulator<VariablesTrading> simulator) throws JOptimizerException {
        Map<Integer, List<SimulationResult<VariablesTrading>>> simulationResultsMap=new HashMap<>();
        for (int i = 0; i < nSim; i++) {
            simulationResultsMap.put(i, simulator.simulateWithNoExplorationEndStateNotEnded());
        }
        return simulationResultsMap;
    }

    public void plotMemory(DisCoMemory<VariablesTrading> critic, String name) {
        TradingMemoryPlotter<VariablesTrading> plotter=
                new TradingMemoryPlotter<>(critic, name,(int) settings.timeEnd());
        plotter.plot();
    }

    public void printing(
            TrainerMultiStepACDC<VariablesTrading> trainer,
            CpuTimer timer) {
        log.info("agent = " + trainer.getAgent());
        timer.stop();
        log.info("timer (ms) = " + timer.getAbsoluteProgress());
    }

}
