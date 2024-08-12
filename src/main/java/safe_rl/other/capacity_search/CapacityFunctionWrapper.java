package safe_rl.other.capacity_search;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import com.joptimizer.exception.JOptimizerException;
import common.math.FunctionWrapperI;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.simulator.AgentSimulator;
import safe_rl.domain.trainer.TrainerMultiStepACDC;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.factories.TrainerSimulatorFactoryTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;

import static safe_rl.persistance.ElDataFinals.*;

@RequiredArgsConstructor
public class CapacityFunctionWrapper implements FunctionWrapperI {

    @NonNull  SettingsTrading settingsTrading;
    @NonNull Double poorValue;
    @NonNull Integer nofEpis;

    Pair<TrainerMultiStepACDC<VariablesTrading>, AgentSimulator<VariablesTrading>> trainerAndSimulator;

    @Override
    public double f(double x) {
        var settings = settingsTrading.withPowerCapacityFcrRange(Range.closed(POWER_MIN, x));

        if (!settings.isDataOk()) {
            return poorValue;
        }

        //Maybe not super-clean construct trainerAndSimulator  here, but alt is the hassle of injecting settings
        trainerAndSimulator = TrainerSimulatorFactoryTrading.trainerSimulatorPairNight(
                settings, nofEpis);

        var trainer = trainerAndSimulator.getFirst();
        var simulator = trainerAndSimulator.getSecond();
        try {
            trainer.train();
            //plotting(settings,trainerAndSimulator);
            return simulator.sumRewardsFromSimulations(N_SIM_START_STATE_EVAL);
        } catch (JOptimizerException e) {
            return poorValue;
        }
    }

    public StateI<VariablesTrading> endStateFromSimulation() throws JOptimizerException {
        Preconditions.checkArgument(trainerAndSimulator!=null,"Need to run capacity optimizer first");
        var simulator = trainerAndSimulator.getSecond();
        return  simulator.endStateFromSingleSimulation();
    }

}
