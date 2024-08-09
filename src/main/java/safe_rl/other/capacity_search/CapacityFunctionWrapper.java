package safe_rl.other.capacity_search;

import com.google.common.collect.Range;
import com.joptimizer.exception.JOptimizerException;
import common.math.FunctionWrapperI;
import common.other.CpuTimer;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.simulator.AgentSimulator;
import safe_rl.domain.trainer.TrainerMultiStepACDC;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.other.runner_helpers.RunnerHelperTrading;

import static safe_rl.persistance.ElDataFinals.*;
import static safe_rl.other.runner_helpers.RunnerHelperTrading.trainerSimulatorPairNight;

@AllArgsConstructor
public class CapacityFunctionWrapper implements FunctionWrapperI {

    SettingsTrading settingsTrading;
    double poorValue;
    int nEpis;

    @Override
    public double f(double x) {
        var settings = settingsTrading.withPowerCapacityFcrRange(Range.closed(POWER_MIN, x));

        if (!settings.isDataOk()) {
            return poorValue;
        }

        var trainerAndSimulator = trainerSimulatorPairNight(settings, DYMMY_N_SIMULATIONS, nEpis);
        var trainer = trainerAndSimulator.getFirst();
        var simulator = trainerAndSimulator.getSecond();
        try {
            trainer.train();
            return simulator.simulationValueInStartState(N_SIM_START_STATE_EVAL);
        } catch (JOptimizerException e) {
            return poorValue;
        }
    }

    private static void plotting(SettingsTrading settings, Pair<TrainerMultiStepACDC<VariablesTrading>, AgentSimulator<VariablesTrading>> trainerAndSimulator) throws JOptimizerException {
        var helper = RunnerHelperTrading.<VariablesTrading>builder()
                .nSim(N_SIMULATIONS_PLOTTING).settings(settings)
                .build();
        helper.plotAndPrint(trainerAndSimulator,new CpuTimer(),"");
    }
}
