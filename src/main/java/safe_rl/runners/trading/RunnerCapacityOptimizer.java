package safe_rl.runners.trading;

import org.apache.commons.math3.util.Pair;
import safe_rl.other.CapacityOptimizer;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.persistance.ElDataHelper;
import static safe_rl.persistance.ElDataFinals.*;
import static safe_rl.runners.trading.RunnerHelperTrading.getSettings;

public class RunnerCapacityOptimizer {
    public static final double DUMMY_CAP = 0d;
    public static int DAY_IDX = 2;

    public static void main(String[] args) {

        var dayId = DAYS.get(DAY_IDX);
        var energyFcrPricePair= ElDataHelper.getPricePair(dayId,FROM_TO_HOUR, Pair.create(FILE_ENERGY,FILE_FCR));
        SettingsTrading settings = getSettings(energyFcrPricePair, DUMMY_CAP, SOC_TERMINAL_MIN);

        var optimizer=new CapacityOptimizer(settings, TOL_GOLDEN_SEARCH);
        double capBest=optimizer.optimize();

        System.out.println("capBest = " + capBest);

    }
}
