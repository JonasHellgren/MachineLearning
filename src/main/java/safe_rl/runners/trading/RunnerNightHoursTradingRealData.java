package safe_rl.runners.trading;

import com.google.common.collect.Range;
import common.list_arrays.ListUtils;
import common.other.CpuTimer;
import lombok.SneakyThrows;
import org.apache.commons.math3.util.Pair;
import safe_rl.environments.factories.SettingsTradingFactory;
import safe_rl.environments.factories.TrainerParametersFactory;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.persistance.ElDataHelper;
import safe_rl.persistance.trade_environment.*;

import java.util.List;

import static safe_rl.persistance.ElDataFinals.FILE_ENERGY;
import static safe_rl.persistance.ElDataFinals.FILE_FCR;

public class RunnerNightHoursTradingRealData {

    public static final ElPriceRepoPlotter.Settings NO_LEGEND_SETTINGS =
            ElPriceRepoPlotter.Settings.newDefault().withIsLegend(false);
    public static final ElPriceRepoPlotter.Settings DEF_SETTINGS =
            ElPriceRepoPlotter.Settings.newDefault();
    public static final Pair<Integer, Integer> FROM_TO_HOUR = Pair.create(17, 8);
    public static final double SOC_TERMINAL_MIN = 0.95;
    public static final double POWER_CAPACITY_FCR = 10d;



    public static int DAY_IDX = 2;
    public static List<DayId> DAYS = List.of(
            DayId.of(24,0,0,"se3"),
            DayId.of(24,0,4,"se3"),
            DayId.of(24,3,8,"se3")  //high fcr price
    );
    public static final int N_SIMULATIONS = 5;
    public static final double SOC_START = 0.5;

    @SneakyThrows
    public static void main(String[] args) {
        var dayId=DAYS.get(DAY_IDX);
        var energyFcrPricePair= ElDataHelper.getPricePair(dayId,FROM_TO_HOUR,Pair.create(FILE_ENERGY,FILE_FCR));

        var settings =SettingsTradingFactory.new100kWhVehicleEmptyPrices()
                .withPowerCapacityFcrRange(Range.closed(0d,POWER_CAPACITY_FCR))
                .withEnergyPriceTraj(ListUtils.toArray(energyFcrPricePair.getFirst()))
                .withCapacityPriceTraj(ListUtils.toArray(energyFcrPricePair.getSecond()))
                .withSocTerminalMin(SOC_TERMINAL_MIN);
        settings.check();
        var helper = RunnerHelperTrading.<VariablesTrading>builder()
                .nSim(N_SIMULATIONS).settings(settings)
                .socStart(SOC_START)
                .build();
        var trainerAndSimulator = helper.createTrainerAndSimulator(
                TrainerParametersFactory.tradingNightHours(300));
        var trainer = trainerAndSimulator.getFirst();
        var timer = CpuTimer.newWithTimeBudgetInMilliSec(0);
        trainer.train();
        helper.plotAndPrint(trainerAndSimulator,timer,dayId.toDateString());
    }
}
