package safe_rl.runners.trading;

import com.joptimizer.exception.JOptimizerException;
import common.list_arrays.ArrayUtil;
import common.list_arrays.ListUtils;
import common.math.BestPairFinder;
import common.other.CpuTimer;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import safe_rl.environments.factories.SettingsTradingFactory;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.persistance.ElDataHelper;
import java.util.*;
import static safe_rl.persistance.ElDataFinals.*;
import static safe_rl.runners.trading.RunnerHelperTrading.getAgentSimulatorPair;
import static safe_rl.runners.trading.RunnerHelperTrading.getSettings;

@Log
public class RunnerCapacityEvaluation {

    public static final int N_CAPS = 5;
    public static int DAY_IDX = 2;


    public static void main(String[] args) {
        var dayId = DAYS.get(DAY_IDX);
        var energyFcrPricePair= ElDataHelper.getPricePair(dayId,FROM_TO_HOUR,Pair.create(FILE_ENERGY,FILE_FCR));
        double powerMax = SettingsTradingFactory.new100kWhVehicleEmptyPrices().powerChargeMax();
        final List<Double> powerCapList = ListUtils.doublesStartEndStep(POWER_MIN, powerMax,powerMax/ N_CAPS);

        Map<Double, Double> capValueMap = new HashMap<>();
        var timer = CpuTimer.newWithTimeBudgetInMilliSec(0);

        for (double cap : powerCapList) {
            SettingsTrading settings = getSettings(energyFcrPricePair, cap, SOC_TERMINAL_MIN);

            if (!settings.isDataOk()) {
                capValueMap.put(cap, POOR_VALUE);
                log.info("bad data");
                continue;
            }

            var trainerAndSimulator = getAgentSimulatorPair(settings, N_SIMULATIONS, SOC_START);
            var trainer = trainerAndSimulator.getFirst();
            var simulator = trainerAndSimulator.getSecond();
            try {
                trainer.train();
                double val = simulator.valueInStartState();
                capValueMap.put(cap, val);
                log.info("all fine");
            } catch (JOptimizerException e) {
                capValueMap.put(cap, POOR_VALUE);
                log.info("failed learning");
            }
        }

        var xyData= ArrayUtil.convertMapToArrays(capValueMap);
        var bestEntry= BestPairFinder.findHighestValuePair(capValueMap);
        var chart = getChart(timer, xyData, bestEntry);
        chart.getStyler().setLegendVisible(false);
        new SwingWrapper<>(chart).displayChart();
    }

    @NotNull
    private static XYChart getChart(CpuTimer timer, Pair<double[], double[]> xyData, Map.Entry<Double, Double> bestEntry) {
        return QuickChart.getChart("Time used (ms)="+ timer.timeInSecondsAsString()+". (cap,value)="+
                        "("+formatter.format(bestEntry.getKey())+
                        ","+formatter.format(bestEntry.getValue())+")"
                , "Cap", "Value",
                "-",
                xyData.getFirst(), xyData.getSecond());
    }


}