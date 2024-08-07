package safe_rl.domain.trainer.recorders;

import common.list_arrays.ListUtils;
import common.other.Conditionals;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;
import policy_gradient_problems.domain.value_classes.ProgressMeasures;
import safe_rl.domain.trainer.value_objects.TrainerParameters;

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
@AllArgsConstructor
public class RecorderTrainingProgress {

    public static final int WIDTH = 200;
    public static final int HEIGHT = 150;
    public static final int N_XTICK_INTERVALS = 5;
    public static final int MIN_N_EPIS_FOR_CLUTTER = 300;
    List<ProgressMeasures> measuresList;
    public static final BitmapEncoder.BitmapFormat FORMAT = BitmapEncoder.BitmapFormat.PNG;
    public static final int DPI = 300;

    TrainerParameters trainerParameters;
    public RecorderTrainingProgress(TrainerParameters trainerParameters) {
        this.trainerParameters=trainerParameters;
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

/*    public List<Integer> nStepsTraj() {
        return measuresList.stream().map(tm -> tm.nSteps()).toList();
    }*/

    public List<Double> sumRewardsTraj() {
        return getMeasure(ProgressMeasures::sumRewards);
    }


    public List<Double> actionChangeTraj() {
        return getMeasure(ProgressMeasures::actionChange);
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
       // charts.add(createChart("nSteps", ints2NumList(nStepsTraj())));
        charts.add(createChart("actionChange", doubles2NumList(actionChangeTraj())));
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
        saveBitmapWithDPI(chartAccRew, path+"/"+"accReward", FORMAT, DPI);
        var chartCriticLoss=createChart("Critic loss", doubles2NumList(criticLossTraj()));
        saveBitmapWithDPI(chartCriticLoss, path+"/"+"criticLoss", FORMAT, DPI);
    }


    private List<Number> ints2NumList(List<Integer> intList) {
        return intList.stream().map(i -> (Number) i).toList();
    }

    private List<Number> doubles2NumList(List<Double> doubleList) {
        return doubleList.stream().map(i -> (Number) i).toList();
    }

    @NotNull
    private  XYChart createChart(String name, List<Number> yData) {
        XYChart chart = new XYChartBuilder()
                .xAxisTitle("Episode").yAxisTitle(name).width(WIDTH).height(HEIGHT).build();
        List<Double> xList= ListUtils.doublesStartStepNitems(0,1,yData.size());
        XYSeries series = chart.addSeries(name, xList, yData);
        series.setMarker(SeriesMarkers.NONE);
        styleChart(chart);
        return chart;
    }


    private void styleChart(XYChart chart) {
        var styler = chart.getStyler();
        styler.setChartBackgroundColor(Color.WHITE);
        styler.setPlotBorderVisible(false);
        styler.setLegendVisible(false);
        int xStep = (trainerParameters.nofEpisodes() / N_XTICK_INTERVALS);
        Conditionals.executeIfTrue(trainerParameters.nofEpisodes()> MIN_N_EPIS_FOR_CLUTTER, () ->
                reduceXAxisTicksClutter(chart, xStep));
    }

    private static void reduceXAxisTicksClutter(XYChart chart, int xStep) {
        Function<Double, String> tickLabelsFormatter = value -> {
            int intValue = value.intValue();
            if (intValue % xStep == 0) {
                return String.valueOf(intValue);
            } else {
                return ""; // Skip labels for other values
            }
        };
        chart.setCustomXAxisTickLabelsFormatter(tickLabelsFormatter);
    }

}
