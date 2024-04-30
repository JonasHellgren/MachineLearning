package safe_rl.runners;

import com.joptimizer.exception.JOptimizerException;
import common.other.CpuTimer;
import lombok.Builder;
import lombok.extern.java.Log;
import safe_rl.domain.memories.DisCoMemory;
import safe_rl.domain.trainers.TrainerMultiStepACDC;
import safe_rl.domain.value_classes.SimulationResult;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.TradeSimulationPlotter;
import safe_rl.environments.trading_electricity.TradingMemoryPlotter;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.helpers.AgentSimulator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Builder
@Log
public class RunnerHelper {

    SettingsTrading settings;
    int nSim;

    public   void simulateAndPlot(AgentSimulator<VariablesTrading> simulator) throws JOptimizerException {
        Map<Integer, List<SimulationResult<VariablesTrading>>> simulationResultsMap=new HashMap<>();
        for (int i = 0; i < nSim; i++) {
            simulationResultsMap.put(i, simulator.simulateWithNoExploration());
        }
        new TradeSimulationPlotter<VariablesTrading>(settings).plot(simulationResultsMap);
    }

    public  void plotMemory(DisCoMemory<VariablesTrading> critic, String name) {
        TradingMemoryPlotter<VariablesTrading> plotter=
                new TradingMemoryPlotter<>(critic, name,(int) settings.timeEnd());
        plotter.plot();
    }

    public  void printing(
            TrainerMultiStepACDC<VariablesTrading> trainer,
            CpuTimer timer) {
        log.info("agent = " + trainer.getAgent());
        timer.stop();
        log.info("timer (ms) = " + timer.getAbsoluteProgress());
    }

}
