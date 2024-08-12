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
import safe_rl.environments.factories.TrainerSimulatorFactoryTrading;
import safe_rl.persistance.ElDataHelper;
import java.util.*;

import static safe_rl.environments.factories.SettingsTradingFactory.getSettingsV2G;
import static safe_rl.persistance.ElDataFinals.*;

@Log
public class RunnerCapacityEvaluation {

    public static final int N_CAPS = 5;
    public static final double VAL_IF_FAIL = -10d;
    public static int DAY_IDX = 2;


    public static void main(String[] args) {
        var dayId = DAYS.get(DAY_IDX);
        var energyFcrPricePair= ElDataHelper.getPricePair(dayId,FROM_TO_HOUR,Pair.create(FILE_ENERGY,FILE_FCR));
        double powerMax = POWER_CHARGE_MAX;
        final List<Double> powerCapList = ListUtils.doublesStartEndStep(POWER_MIN, powerMax,powerMax/ N_CAPS);

        Map<Double, Double> capValueMap = new HashMap<>();
        var timer = CpuTimer.newWithTimeBudgetInMilliSec(0);

        System.out.println("powerCapList = " + powerCapList);

        for (double cap : powerCapList) {
            var settings = getSettingsV2G(
                    energyFcrPricePair, cap, SOC_START, SOC_DELTA, powerMax, PRICE_BATTERY);

            if (!settings.isDataOk()) {
                capValueMap.put(cap, VAL_IF_FAIL);
                log.info("bad data");
                continue;
            }

            var trainerAndSimulator = TrainerSimulatorFactoryTrading.trainerSimulatorPairNight(
                    settings, NOF_EPISODES);
            var trainer = trainerAndSimulator.getFirst();
            var simulator = trainerAndSimulator.getSecond();
            try {
                log.info("Training started");
                trainer.train();
                log.info("Training finished");
                double val = simulator.sumRewardsFromSimulations(N_SIM_START_STATE_EVAL);
                capValueMap.put(cap, val);
                log.info("all fine");
            } catch (JOptimizerException e) {
                capValueMap.put(cap, VAL_IF_FAIL);
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