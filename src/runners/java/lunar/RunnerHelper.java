package lunar;

import book_rl_explained.lunar_lander.domain.agent.AgentLunar;
import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.environment.EnvironmentLunar;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierFromMaxHeight;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierRandomAndClipped;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierRandomHeightZeroSpeed;
import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import book_rl_explained.lunar_lander.domain.trainer.TrainerI;
import book_rl_explained.lunar_lander.domain.trainer.TrainerParameters;
import book_rl_explained.lunar_lander.helpers.AgentEvaluator;
import book_rl_explained.lunar_lander.helpers.PlotterAgent;
import book_rl_explained.lunar_lander.helpers.PlotterProgressMeasures;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.Pair;
import org.hellgren.plotters.chart_saver.ChartSaver;
import org.hellgren.utilities.file_io.PathAndFile;
import org.hellgren.utilities.list_arrays.ListConverter;
import org.hellgren.utilities.list_arrays.ListCreator;
import org.hellgren.utilities.list_arrays.MyListUtils;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RunnerHelper {

    public static final double MIN_START_HEIGHT = 3d;
    public static final double MIN_START_SPD = -2d;
    public static final double MAX_START_SPD = -0d;
    public static final String CHART_DIR = "src/runners/java/lunar/pics/";



    record ResultsNStep(Map<Integer, List<Double>> resultMap) {
        public static ResultsNStep empty() {
            return new ResultsNStep(new HashMap<>());
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

    static void plotNStepResults(ResultsNStep results, List<Integer> nStepsList) {
        var xList= ListConverter.integer2Double(nStepsList);
        var yList = nStepsList.stream()
                .map(nSteps -> results.average(nSteps)).toList();
        var errList = nStepsList.stream()
                .map(nSteps -> results.maxMinusMin(nSteps)).toList();
        var chart = new XYChartBuilder()
                .width(600).height(200)
                .title("").xAxisTitle("N Steps").yAxisTitle("Avg. sumRewards")
                .build();
        var styler = chart.getStyler();
        styler.setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        styler.setChartTitleVisible(false);
        styler.setLegendVisible(false);
        styler.setAxisTitlesVisible(true);
        styler.setXAxisDecimalPattern("0");
        styler.setChartBackgroundColor(Color.WHITE);
        XYSeries series = chart.addSeries("a", xList, yList, errList);
        series.setMarkerColor(Color.GRAY);
        series.setMarker(SeriesMarkers.SQUARE);
        new SwingWrapper<>(chart).displayChart();
        ChartSaver.saveXYChart(chart, PathAndFile.ofPng(CHART_DIR, "lunar_step_horizon_evaluator.png"));

    }


    public static void evaluate(TrainerDependencies trainerDependencies, LunarProperties ep) {
        trainerDependencies = trainerDependencies.withStartStateSupplier(StartStateSupplierRandomHeightZeroSpeed.create(ep));
        var evaluatorFails = AgentEvaluator.of(trainerDependencies);
        double fractionFails= evaluatorFails.fractionFails(10);
        System.out.println("fractionFails = " + fractionFails);
        trainerDependencies = trainerDependencies.withStartStateSupplier(StartStateSupplierFromMaxHeight.create(ep));
        var evaluatorSim = AgentEvaluator.of(trainerDependencies);
        evaluatorSim.plotAndSavePicFromSimulation(CHART_DIR);
    }


    static TrainerDependencies getDependencies(LunarProperties ep, int stepHorizon, int nEpisodes) {
        var p = AgentParameters.newDefault(ep);
        var tp = TrainerParameters.newDefault().withStepHorizon(stepHorizon).withNEpisodes(nEpisodes);
        return TrainerDependencies.builder()
                .agent(AgentLunar.zeroWeights(p, ep))
                .environment(EnvironmentLunar.of(ep))
                .trainerParameters(tp)
                .startStateSupplier(StartStateSupplierRandomAndClipped.create(
                        ep, Pair.create(MIN_START_HEIGHT, ep.yMax()), Pair.create(MIN_START_SPD, MAX_START_SPD)))
                .build();
    }

    public static final PlotterAgent.Settings SETTINGS_PLOTTER_AGENT = PlotterAgent.Settings.builder()
            .nY(50).nSpd(50).nDigitsAxisLabels(1).valueFormat("%.1f").build();

    static void plot(TrainerI trainer, TrainerDependencies trainerDependencies) {
        var progressPlotter = PlotterProgressMeasures.of(trainer);
        progressPlotter.plot();
        var agentPlotter = PlotterAgent.of(trainerDependencies, SETTINGS_PLOTTER_AGENT);
        agentPlotter.plotAndSaveAll();
    }


}
