package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.trainer.TrainerLunar;
import book_rl_explained.lunar_lander.domain.trainer.TrainerParameters;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.hellgren.utilities.conditionals.Conditionals;
import org.hellgren.utilities.list_arrays.ListCreator;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import static org.knowm.xchart.BitmapEncoder.saveBitmapWithDPI;

@Log
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlotterProgressMeasures {

    public static final int WIDTH = 200;
    public static final int HEIGHT = 150;
    public static final int N_XTICK_INTERVALS = 5;
    public static final int MIN_N_EPIS_FOR_CLUTTER = 300;

    RecorderTrainingProgress recorder;
    TrainerParameters trainerParameters;

    public static PlotterProgressMeasures of(RecorderTrainingProgress recorder,
                                             TrainerParameters trainerParameters) {
        return new PlotterProgressMeasures(recorder,trainerParameters);
    }

    public static PlotterProgressMeasures of(TrainerLunar trainer) {
        return new PlotterProgressMeasures(
                trainer.getRecorder(),
                trainer.getDependencies().trainerParameters());
    }


    public void plot(String title) {
        if (recorder.isEmpty()) {
            log.warning("No training progress data to plot");
            return;
        }
        List<XYChart> charts = new ArrayList<>();
        charts.add(createChart("accum reward", recorder.trajOf("sumRewards")));
        charts.add(createChart("nSteps", recorder.trajOf("nSteps")));
        charts.add(createChart("tdErr", recorder.trajOf("tdError")));
        charts.add(createChart("stdActor", recorder.trajOf("stdActor")));
        var frame= new SwingWrapper<>(charts).displayChartMatrix();
        frame.setTitle(title);
    }
/*

    @SneakyThrows
    public void saveCharts(String path) {
        var chartAccRew=createChart("Acc. reward", doubles2NumList(sumRewardsTraj()));
        saveBitmapWithDPI(chartAccRew, path+"/"+"accReward", FORMAT, DPI);
        var chartCriticLoss=createChart("Critic loss", doubles2NumList(criticLossTraj()));
        saveBitmapWithDPI(chartCriticLoss, path+"/"+"criticLoss", FORMAT, DPI);
    }
*/


    private List<Number> ints2NumList(List<Integer> intList) {
        return intList.stream().map(i -> (Number) i).toList();
    }

    private List<Number> doubles2NumList(List<Double> doubleList) {
        return doubleList.stream().map(i -> (Number) i).toList();
    }

    @NotNull
    private  XYChart createChart(String name, List<Double> yData) {
        XYChart chart = new XYChartBuilder()
                .xAxisTitle("Episode").yAxisTitle(name).width(WIDTH).height(HEIGHT).build();
        List<Double> xList= ListCreator.createFromStartWithStepWithNofItems(0d,1d,yData.size());
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
        int xStep = recorder.nSteps() / N_XTICK_INTERVALS;
        Conditionals.executeIfTrue(trainerParameters.nEpisodes()> MIN_N_EPIS_FOR_CLUTTER, () ->
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
