package lunar;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.trainer.TrainerLunarMultiStep;
import lombok.extern.java.Log;
import org.hellgren.utilities.list_arrays.ListCreator;
import org.hellgren.utilities.list_arrays.MyListUtils;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lunar.RunnerHelper.*;

@Log
public class RunnerStepHorizonEvaluator {
    public static final int N_EPISODES = 5_000;
    public static final int N_EVALS_PER_N = 3;
    static final List<Integer> N_STEPS_LIST = List.of(1, 4, 7, 10);

    record Results(Map<Integer, List<Double>> resultMap) {
        public static Results empty() {
            return new Results(new HashMap<>());
        }

        public void add(int nSteps, double sumRewardAverage) {
            resultMap.computeIfAbsent(nSteps, k -> ListCreator.emptyDouble()).add(sumRewardAverage);
        }

        public double average(int nSteps) {
            return MyListUtils.findAverage(resultMap.get(nSteps)).orElseThrow();
        }

        public double maxMinusMin(int nSteps) {
            double max = MyListUtils.findMax(resultMap.get(nSteps)).orElseThrow();
            double min = MyListUtils.findMin(resultMap.get(nSteps)).orElseThrow();
            return max - min;
        }
    }

    public static void main(String[] args) {
        var ep = LunarProperties.defaultProps();
        var results = evaluateNSteps(ep, N_STEPS_LIST);
        plotResults(results);
    }

    static Results evaluateNSteps(LunarProperties ep, List<Integer> nStepsList) {
        var results = Results.empty();
        for (var nSteps : nStepsList) {
            log.info("nSteps: " + nSteps);
            for (int i = 0; i < N_EVALS_PER_N; i++) {
                var trainer = TrainerLunarMultiStep.of(getDependencies(ep, nSteps, N_EPISODES));
                trainer.train();
                var sumRewardsList = trainer.getRecorder().trajectory("sumRewards");
                double sumRewardAverage = MyListUtils.findAverage(sumRewardsList).orElseThrow();
                results.add(nSteps, sumRewardAverage);
            }
        }
        return results;
    }

    private static void plotResults(Results results) {
        var yList = N_STEPS_LIST.stream()
                .map(nSteps -> results.average(nSteps)).toList();
        var errList = N_STEPS_LIST.stream()
                .map(nSteps -> results.maxMinusMin(nSteps)).toList();
        var chart = new XYChartBuilder()
                .width(400).height(200)
                .title("ErrorChart").xAxisTitle("n Steps").yAxisTitle("avg. sumRewards")
                .build();
        var styler = chart.getStyler();
        styler.setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        styler.setChartTitleVisible(false);
        styler.setLegendVisible(false);
        styler.setAxisTitlesVisible(true);
        styler.setXAxisDecimalPattern("0");
        XYSeries series = chart.addSeries("a", N_STEPS_LIST, yList, errList);
        series.setMarkerColor(Color.GRAY);
        series.setMarker(SeriesMarkers.SQUARE);
        new SwingWrapper<>(chart).displayChart();
    }


}
