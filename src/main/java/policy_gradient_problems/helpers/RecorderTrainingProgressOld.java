package policy_gradient_problems.helpers;

import com.beust.jcommander.internal.Lists;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;
import policy_gradient_problems.domain.value_classes.ProgressMeasures;

import java.util.List;
import java.util.function.Function;

/**
 * During training, it is good to keep track of nof steps, accumulated rewards per episode, etc
 * This class is for recording, and later enabling plotting, of such measures
 */

@Log
public class RecorderTrainingProgressOld {

    public static final int WIDTH = 300;
    public static final int HEIGHT = 150;
    List<ProgressMeasures> measuresList= Lists.newArrayList();


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
        List<XYChart> charts = Lists.newArrayList();
        charts.add(createChart("nSteps", ints2NumList(nStepsTraj())));
        charts.add(createChart("accum reward", doubles2NumList(sumRewardsTraj())));
        charts.add(createChart("eval", doubles2NumList(evalTraj())));
        charts.add(createChart("actor loss", doubles2NumList(actorLossTraj())));
        charts.add(createChart("critic loss", doubles2NumList(criticLossTraj())));
        charts.add(createChart("entropy", doubles2NumList(entropyTraj())));

        var frame= new SwingWrapper<>(charts).displayChartMatrix();
        frame.setTitle(title);
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
                .xAxisTitle("episode").yAxisTitle(name).width(WIDTH).height(HEIGHT).build();
        XYSeries series = chart.addSeries(name, null, yData);
        chart.getStyler().setLegendVisible(false);
        series.setMarker(SeriesMarkers.NONE);
        return chart;
    }


}
