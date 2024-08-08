package safe_rl.other;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import com.joptimizer.exception.JOptimizerException;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import safe_rl.environments.trading_electricity.SettingsTrading;

import static safe_rl.runners.trading.RunnerHelperTrading.trainerSimulatorPairNight;

/**
 * https://web2.qatar.cmu.edu/~gdicaro/15382/additional/one-dimensional-search-methods.pdf
 */
@Log
@AllArgsConstructor
public class CapacityOptimizerOld {
    public static final int DYMMY_N_SIMULATIONS = 1;
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    private static final double RESPHI = 2 - PHI;
    public static final int DUMMY_SOC = 0;
    public static final double POWER_MIN = 0d;

    SettingsTrading settingsTrading;
    double poorValue;

    public double optimize(double tol) {
        return goldenSectionSearchMax(POWER_MIN, settingsTrading.minAbsolutePowerCharge(), tol);
    }

    public double goldenSectionSearchMax(double a, double b, double tol) {
        Preconditions.checkArgument(a<b,"Bad interval");

        log.info("First interval, (a,b)=" + "(" + a + "," + b + ")");

        double c = a + RESPHI * (b - a);  //0+0.4*20=8
        double d = b - RESPHI * (b - a);  //22-0.4*20=14

        System.out.println("RESPHI = " + RESPHI);

        while (Math.abs(b - a) > tol) {
            Preconditions.checkArgument(c<d,"Bad interval"+", c="+c+", d="+d);

            double fC = f(c);
            double fD = f(d);
            log.info("c = " + c+", fC = " + fC);
            log.info("d = " + d+", fD = " + fD);
            if (fC > fD) {
                b = d;
            } else {
                a = c;
            }
            log.info("New interval, (a,b)=" + "(" + a + "," + b + ")");

            c = a+ RESPHI * (b - a);
            d = b- RESPHI * (b - a);
        }

        return (b + a) / 2;
    }

    public double f(double x) {
        var settings = settingsTrading.withPowerCapacityFcrRange(Range.closed(POWER_MIN, x));
        if (!settings.isDataOk()) {
            return poorValue;
        }

        var trainerAndSimulator = trainerSimulatorPairNight(settings, DYMMY_N_SIMULATIONS, DUMMY_SOC, 300);
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
