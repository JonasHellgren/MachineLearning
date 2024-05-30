package policy_gradient_problems.helpers;

import common.list_arrays.ListUtils;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;
import policy_gradient_problems.domain.value_classes.ProgressMeasures;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.knowm.xchart.BitmapEncoder.saveBitmapWithDPI;

/**
 * During training, it is good to keep track of nof steps, accumulated rewards per episode, etc
 * This class is for recording, and later enabling plotting, of such measures
 */

@Log
public class RecorderTrainingProgress {

    public static final int WIDTH = 200;
    public static final int HEIGHT = 150;
    List<ProgressMeasures> measuresList;
    public static final BitmapEncoder.BitmapFormat FORMAT = BitmapEncoder.BitmapFormat.PNG;
    public static final int DPI = 300;

    public RecorderTrainingProgress() {
        this.measuresList = new ArrayList<>();
    }

    public void clear() {
        measuresList.clear();
    }

    public void add(ProgressMeasures measures) {
        measuresList.add(measures);
    }

    public int size() {
        return measuresList.size();
    }

    public boolean isEmpty() {
        return measuresList.isEmpty();
    }

    public List<Integer> nStepsTraj() {
        return measuresList.stream().map(tm -> tm.nSteps()).toList();
    }

    public List<Double> sumRewardsTraj() {
        return getMeasure(ProgressMeasures::sumRewards);
    }

    public List<Double> actorLossTraj() {
        return getMeasure(ProgressMeasures::actorLoss);
    }

    public List<Double> criticLossTraj() {
        return getMeasure(ProgressMeasures::criticLoss);
    }

    public List<Double> entropyTraj() {
        return getMeasure(ProgressMeasures::entropy);
    }

    public List<Double> evalTraj() {
        return getMeasure(ProgressMeasures::eval);
    }


    List<Double> getMeasure(Function<ProgressMeasures, Double> function) {
        return measuresList.stream().map(function).toList();
    }

    public void plot(String title) {
        if (isEmpty()) {
            log.warning("No training progress data to plot");
            return;
        }
        List<XYChart> charts = new ArrayList<>();
        charts.add(createChart("nSteps", ints2NumList(nStepsTraj())));
        charts.add(createChart("accum reward", doubles2NumList(sumRewardsTraj())));
        charts.add(createChart("eval", doubles2NumList(evalTraj())));
        charts.add(createChart("actor loss", doubles2NumList(actorLossTraj())));
        charts.add(createChart("critic loss", doubles2NumList(criticLossTraj())));
        charts.add(createChart("entropy", doubles2NumList(entropyTraj())));

        var frame= new SwingWrapper<>(charts).displayChartMatrix();
        frame.setTitle(title);
    }

    @SneakyThrows
    public void saveCharts(String path) {
        var chartAccRew=createChart("Acc. reward", doubles2NumList(sumRewardsTraj()));
        styleChart(chartAccRew);
        saveBitmapWithDPI(chartAccRew, path+"/"+"accReward", FORMAT, DPI);
        var chartCriticLoss=createChart("Critic loss", doubles2NumList(criticLossTraj()));
        styleChart(chartCriticLoss);
        saveBitmapWithDPI(chartCriticLoss, path+"/"+"criticLoss", FORMAT, DPI);
    }

    private static void styleChart(XYChart chart) {
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setPlotBorderVisible(false);
        reduceXAxisTicksClutter(chart, 1000);
    }

    private static void reduceXAxisTicksClutter(XYChart chart, int i) {
        chart.setCustomXAxisTickLabelsFormatter(value -> {
            int intValue = value.intValue();
            if (intValue % i == 0) {
                return String.valueOf(intValue);
            } else {
                return ""; // Skip labels for other values
            }
        });
    }

    private List<Number> ints2NumList(List<Integer> intList) {
        return intList.stream().map(i -> (Number) i).toList();
    }

    private List<Number> doubles2NumList(List<Double> doubleList) {
        return doubleList.stream().map(i -> (Number) i).toList();
    }

    @NotNull
    private static XYChart createChart(String name, List<Number> yData) {
        XYChart chart = new XYChartBuilder()
                .xAxisTitle("Episode").yAxisTitle(name).width(WIDTH).height(HEIGHT).build();
        List<Double> xList= ListUtils.doublesStartStepNitems(0,1,yData.size());
        XYSeries series = chart.addSeries(name, xList, yData);
        chart.getStyler().setLegendVisible(false);
        series.setMarker(SeriesMarkers.NONE);
        return chart;
    }


}
