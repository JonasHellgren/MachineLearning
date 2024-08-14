package safe_rl.runners.trading;

import common.other.CpuTimer;
import lombok.SneakyThrows;
import org.apache.commons.math3.util.Pair;
import safe_rl.environments.factories.AgentParametersFactory;
import safe_rl.environments.factories.SettingsTradingFactory;
import safe_rl.environments.factories.TrainerParametersFactory;
import safe_rl.environments.trading_electricity.ElectricPricePlotter;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.other.runner_helpers.PlotterSaverAndPrinterTrading;
import safe_rl.environments.factories.TrainerSimulatorFactoryTrading;
import safe_rl.persistance.ElDataHelper;
import safe_rl.persistance.trade_environment.DayId;

import java.util.List;

import static safe_rl.persistance.ElDataFinals.*;

public class RunnerNightHoursTradingRealData {

    static final Pair<Integer, Integer> FROM_TO_HOUR = Pair.create(17, 8);
    static int DAY_IDX = 1;
    static boolean IS_G2V =false;

    @SneakyThrows
    public static void main(String[] args) {
        var dayId = DAYS_CLUSTER_ANALYSIS.get(DAY_IDX);
        var fromToHour = FROM_TO_HOUR;
        var energyFcrPricePair= ElDataHelper.getPricePair(dayId, fromToHour,Pair.create(FILE_ENERGY,FILE_FCR));
        double powerChargeMax=POWER_CHARGE_MAX2;
        double cap=powerChargeMax/2;

        var settings= IS_G2V
                ? SettingsTradingFactory.getSettingsG2V(
                        energyFcrPricePair, SOC_START, SOC_DELTA, powerChargeMax, PRICE_BATTERY)
                : SettingsTradingFactory.getSettingsV2G(
                        energyFcrPricePair, cap, SOC_START, SOC_DELTA, powerChargeMax, PRICE_BATTERY);
        settings.throwExceptionIfNonCorrect();

        plotPrices(dayId, fromToHour, energyFcrPricePair, settings);

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

    private static void plotPrices(DayId dayId, Pair<Integer, Integer> fromToHour, Pair<List<Double>, List<Double>> energyFcrPricePair, SettingsTrading settings) {
        ElectricPricePlotter.builder()
                .dayId(dayId).fromToHour(fromToHour).energyFcrPricePair(energyFcrPricePair)
                .settings(settings)
                .build().plot();
    }
}
