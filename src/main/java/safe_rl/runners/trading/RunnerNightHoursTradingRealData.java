package safe_rl.runners.trading;

import com.google.common.collect.Range;
import common.list_arrays.ListUtils;
import common.other.CpuTimer;
import lombok.SneakyThrows;
import org.apache.commons.math3.util.Pair;
import safe_rl.environments.factories.AgentParametersFactory;
import safe_rl.environments.factories.SettingsTradingFactory;
import safe_rl.environments.factories.TrainerParametersFactory;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.other.runner_helpers.PlotterSaverAndPrinterTrading;
import safe_rl.environments.factories.TrainerSimulatorFactoryTrading;
import safe_rl.persistance.ElDataHelper;
import safe_rl.persistance.trade_environment.*;

import static safe_rl.persistance.ElDataFinals.*;

public class RunnerNightHoursTradingRealData {

    public static final ElPriceRepoPlotter.Settings NO_LEGEND_SETTINGS =
            ElPriceRepoPlotter.Settings.newDefault().withIsLegend(false);
    public static final ElPriceRepoPlotter.Settings DEF_SETTINGS =
            ElPriceRepoPlotter.Settings.newDefault();
    public static final Pair<Integer, Integer> FROM_TO_HOUR = Pair.create(17, 8);
    public static final double POWER_CHARGE = 22d;
    public static final double POWER_CAPACITY_FCR = POWER_CHARGE/2;

    public static int DAY_IDX = 1;


    @SneakyThrows
    public static void main(String[] args) {
        var dayId = DAYS_CLUSTER_ANALYSIS.get(DAY_IDX);
        var energyFcrPricePair= ElDataHelper.getPricePair(dayId,FROM_TO_HOUR,Pair.create(FILE_ENERGY,FILE_FCR));

        var settings0 =SettingsTradingFactory.new100kWhVehicleEmptyPrices()
                .withPowerCapacityFcrRange(Range.closed(0d,POWER_CAPACITY_FCR))
                .withPowerChargeRange(Range.closed(-POWER_CHARGE, POWER_CHARGE))
                .withEnergyPriceTraj(ListUtils.toArray(energyFcrPricePair.getFirst()))
                .withCapacityPriceTraj(ListUtils.toArray(energyFcrPricePair.getSecond()))
                .withSocStart(SOC_START).withSocDelta(SOC_DELTA)
                .withPriceBattery(PRICE_BATTERY)
                .withFromToHour(FROM_TO_HOUR);
        var settings=settings0
                .withPowerCapacityFcrRange(Range.closed(0d, 0d))
                .withPowerChargeRange(Range.closed(0d, POWER_CHARGE));
        settings.check();
        var helper = PlotterSaverAndPrinterTrading.<VariablesTrading>builder()
                .nSim(N_SIMULATIONS_PLOTTING).settings(settings)
                .build();
        var trainerAndSimulator = TrainerSimulatorFactoryTrading.<VariablesTrading>createTrainerAndSimulator(
                TrainerParametersFactory.tradingNightHours(NOF_EPISODES),
                AgentParametersFactory.trading24Hours(settings), settings);
        var trainer = trainerAndSimulator.getFirst();
        var timer = CpuTimer.newWithTimeBudgetInMilliSec(0);
        trainer.train();
        helper.plotAndPrint(trainerAndSimulator,timer,dayId.toDateString());
    }
}
