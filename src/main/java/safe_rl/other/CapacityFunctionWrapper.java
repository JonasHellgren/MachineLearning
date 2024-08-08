package safe_rl.other;

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
import safe_rl.runners.trading.RunnerHelperTrading;
import static safe_rl.persistance.ElDataFinals.SOC_START;
import static safe_rl.runners.trading.RunnerHelperTrading.getAgentSimulatorPair;

@AllArgsConstructor
public class CapacityFunctionWrapper implements FunctionWrapperI {
    public static final int DYMMY_N_SIMULATIONS = 1;
    public static final double POWER_MIN = 0d;

    SettingsTrading settingsTrading;
    double poorValue;

    @Override
    public double f(double x) {
        var settings = settingsTrading.withPowerCapacityFcrRange(Range.closed(POWER_MIN, x));

        if (!settings.isDataOk()) {
            return poorValue;
        }

        var trainerAndSimulator = getAgentSimulatorPair(settings, DYMMY_N_SIMULATIONS, SOC_START);
        var trainer = trainerAndSimulator.getFirst();
        var simulator = trainerAndSimulator.getSecond();
        try {
            trainer.train();
            plotting(settings, trainerAndSimulator);
            return simulator.valueInStartState();
        } catch (JOptimizerException e) {
            return poorValue;
        }
    }

    private static void plotting(SettingsTrading settings, Pair<TrainerMultiStepACDC<VariablesTrading>, AgentSimulator<VariablesTrading>> trainerAndSimulator) throws JOptimizerException {
        var helper = RunnerHelperTrading.<VariablesTrading>builder()
                .nSim(5).settings(settings)
                .socStart(SOC_START)
                .build();
        helper.plotAndPrint(trainerAndSimulator,new CpuTimer(),"");
    }
}
