package safe_rl.capacity_optimizer;

import com.google.common.collect.Range;
import com.joptimizer.exception.JOptimizerException;
import lombok.extern.java.Log;
import safe_rl.environments.trading_electricity.SettingsTrading;

import static safe_rl.runners.trading.RunnerHelperTrading.getAgentSimulatorPair;

@Log
public class CapacityOptimizer {
    public static final int DYMMY_N_SIMULATIONS = 1;
    private static final double PHI = (DYMMY_N_SIMULATIONS + Math.sqrt(5)) / 2;
    private static final double RESPHI = 2 - PHI;
    public static final int DUMMY_SOC = 0;
    public static final double _POWER_MIN = 0d;

    SettingsTrading settingsTrading;
    double poorValue;

    public double optimize(double tol) {
        return goldenSectionSearchMax(_POWER_MIN, settingsTrading.powerChargeMax(), tol);
    }

    public double goldenSectionSearchMax(double a, double b, double tol) {
        double c = b - RESPHI * (b - a);
        double d = a + RESPHI * (b - a);

        while (Math.abs(b - a) > tol) {
            if (f(c) > f(d)) {
                b = d;
            } else {
                a = c;
            }
            log.info("New interval, (a,b)=" + "(" + a + "," + b + ")");
            c = b - RESPHI * (b - a);
            d = a + RESPHI * (b - a);
        }

        return (b + a) / 2;
    }

    public double f(double x) {
        var settings = settingsTrading.withPowerCapacityFcrRange(Range.closed(_POWER_MIN, x));
        if (!settings.isDataOk()) {
            return poorValue;
        }

        var trainerAndSimulator = getAgentSimulatorPair(settings, DYMMY_N_SIMULATIONS, DUMMY_SOC);
        var trainer = trainerAndSimulator.getFirst();
        var simulator = trainerAndSimulator.getSecond();
        try {
            trainer.train();
            return simulator.valueInStartState();
        } catch (JOptimizerException e) {
            return poorValue;
        }
    }


}
