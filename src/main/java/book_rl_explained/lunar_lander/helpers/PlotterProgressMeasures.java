package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.trainer.TrainerI;
import book_rl_explained.lunar_lander.domain.trainer.TrainerLunarSingleStep;
import book_rl_explained.lunar_lander.domain.trainer.TrainerParameters;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.hellgren.utilities.conditionals.Conditionals;
import org.hellgren.utilities.list_arrays.ListCreator;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

@Log
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlotterProgressMeasures {

    public static final int WIDTH = 200;
    public static final int HEIGHT = 150;
    public static final int N_XTICK_INTERVALS = 5;
    public static final int MIN_N_EPIS_FOR_CLUTTER = 500;

    RecorderTrainingProgress recorder;
    TrainerParameters trainerParameters;

    public static PlotterProgressMeasures of(RecorderTrainingProgress recorder,
                                             TrainerParameters trainerParameters) {
        return new PlotterProgressMeasures(recorder,trainerParameters);
    }

    public static PlotterProgressMeasures of(TrainerI trainer) {
        return new PlotterProgressMeasures(
                trainer.getRecorder(),
                trainer.getDependencies().trainerParameters());
    }


    public void plot(String title) {
        Preconditions.checkArgument(!recorder.isEmpty(), "No training progress data to plot");
        var measures=List.of("sumRewards","stateValuePos2Spd0","stateValuePos5Spd2","nSteps","tdError","stdActor");
        var charts = measures.stream()
                .map(measure -> createChart(measure, recorder.trajectory(measure)))
                .toList();
        var frame= new SwingWrapper<>(charts).displayChartMatrix();
        frame.setTitle(title);
    }

    private  XYChart createChart(String name, List<Double> yData) {
        var chart = new XYChartBuilder()
                .xAxisTitle("Episode").yAxisTitle(name).width(WIDTH).height(HEIGHT).build();
        var xList= ListCreator.createFromStartWithStepWithNofItems(0d,1d,yData.size());
        var series = chart.addSeries(name, xList, yData);
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
