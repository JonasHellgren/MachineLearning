package safe_rl.other.capacity_search;

import com.google.common.collect.Range;
import com.joptimizer.exception.JOptimizerException;
import common.math.FunctionWrapperI;
import lombok.AllArgsConstructor;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.factories.TrainerSimulatorFactoryTrading;

import static safe_rl.persistance.ElDataFinals.*;

@AllArgsConstructor
public class CapacityFunctionWrapper implements FunctionWrapperI {

    SettingsTrading settingsTrading;
    double poorValue;
    int nofEpis;

    @Override
    public double f(double x) {
        var settings = settingsTrading.withPowerCapacityFcrRange(Range.closed(POWER_MIN, x));

        if (!settings.isDataOk()) {
            return poorValue;
        }

        //Maybe not super-clean construct trainerAndSimulator  here, but alt is the hassle of injecting settings
        var trainerAndSimulator = TrainerSimulatorFactoryTrading.trainerSimulatorPairNight(
                settings, nofEpis);

        var trainer = trainerAndSimulator.getFirst();
        var simulator = trainerAndSimulator.getSecond();
        try {
            trainer.train();
            //plotting(settings,trainerAndSimulator);
            return simulator.simulationValueInStartState(N_SIM_START_STATE_EVAL);
        } catch (JOptimizerException e) {
            return poorValue;
        }
    }

}
