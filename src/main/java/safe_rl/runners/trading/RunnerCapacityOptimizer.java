package safe_rl.runners.trading;

import org.apache.commons.math3.util.Pair;
import safe_rl.other.capacity_search.CapacityOptimizer;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.persistance.ElDataHelper;

import static safe_rl.environments.factories.SettingsTradingFactory.getSettingsV2G;
import static safe_rl.persistance.ElDataFinals.*;

public class RunnerCapacityOptimizer {
    public static int DAY_IDX = 2;

    public static void main(String[] args) {

        var dayId = DAYS.get(DAY_IDX);
        var energyFcrPricePair= ElDataHelper.getPricePair(dayId,FROM_TO_HOUR, Pair.create(FILE_ENERGY,FILE_FCR));
        SettingsTrading settings = getSettingsV2G(
                energyFcrPricePair, DUMMY_CAP, SOC_TERMINAL_MIN,POWER_CHARGE_MAX, PRICE_BATTERY);

        var optimizer=new CapacityOptimizer(settings, TOL_GOLDEN_SEARCH,N_EPIS);
        var capBest=optimizer.optimize();

        System.out.println("capBest = " + capBest);

    }
}
