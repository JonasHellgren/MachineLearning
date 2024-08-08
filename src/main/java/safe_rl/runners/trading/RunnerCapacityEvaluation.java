package safe_rl.runners.trading;

import com.google.common.collect.Range;
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
import safe_rl.persistance.trade_environment.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import static safe_rl.persistance.ElDataFinals.*;
import static safe_rl.runners.trading.RunnerHelperTrading.getAgentSimulatorPair;
import static safe_rl.runners.trading.RunnerHelperTrading.getSettings;

@Log
public class RunnerCapacityEvaluation {

    public static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.US); //US <=> only dots
    public static final DecimalFormat formatter = new DecimalFormat("#.#", SYMBOLS);

    static final String PATH = "src/main/java/safe_rl/persistance/data/";

    public static final double POOR_VALUE = -100d;
    public static final int N_CAPS = 5;

    static PathAndFile fileEnergy = PathAndFile.xlsxOf(PATH, "Day-ahead-6months-EuroPerMWh");
    static PathAndFile fileFcr = PathAndFile.xlsxOf(PATH, "FCR-N-6months-EuroPerMW");

    public static int DAY_IDX = 2;
    public static final int N_SIMULATIONS = 5;
    public static final double SOC_START = 0.5;

    public static void main(String[] args) {
        var dayId = DAYS.get(DAY_IDX);
        var energyFcrPricePair= ElDataHelper.getPricePair(dayId,FROM_TO_HOUR,Pair.create(FILE_ENERGY,FILE_FCR));
        double powerMax = SettingsTradingFactory.new100kWhVehicleEmptyPrices().powerChargeMax();
        final List<Double> powerCapList = ListUtils.doublesStartEndStep(0, powerMax,powerMax/ N_CAPS);

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