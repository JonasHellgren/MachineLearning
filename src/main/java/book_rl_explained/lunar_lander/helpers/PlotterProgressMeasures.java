package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.trainer.TrainerI;
import book_rl_explained.lunar_lander.domain.trainer.TrainerParameters;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.hellgren.plotters.plotting_2d.ErrorBandCreator;
import org.hellgren.utilities.conditionals.Conditionals;
import org.hellgren.utilities.list_arrays.ArrayCreator;
import org.hellgren.utilities.list_arrays.List2ArrayConverter;
import org.hellgren.utilities.list_arrays.ListCreator;
import org.hellgren.utilities.list_arrays.MyListUtils;
import org.hellgren.utilities.math.MovingAverage;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.hellgren.utilities.list_arrays.MyListUtils.elementSubtraction;

@Log
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlotterProgressMeasures {

    public static final int N_WINDOWS = 10;
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


    public void plot() {
        Preconditions.checkArgument(!recorder.isEmpty(), "No training progress data to plot");
        var measures=List.of("sumRewards","tdError","stdActor");
        for (String measure : measures) {
            List<Double> yData0 = recorder.trajectory(measure);
            int length = yData0.size();
            var yDataFiltered= filter(yData0,length/ N_WINDOWS);
            var errData= elementSubtraction(yDataFiltered, yData0);
            var errDataFiltered= filter(errData,length/ N_WINDOWS);
            showPlot(measure, yDataFiltered, errDataFiltered);
        }
    }

    private static void showPlot(String measure, List<Double> yDataFiltered, List<Double> errDataFiltered) {
        var settings = ErrorBandCreator.Settings.ofDefaults()
                .withTitle(measure).withYAxisLabel(measure).withXAxisLabel("Episode").withShowLegend(false);
        var creator = ErrorBandCreator.newOfSettings(settings);
        var yDataFilteredArr= List2ArrayConverter.convertListToDoubleArr(yDataFiltered);
        var errDataArr= List2ArrayConverter.convertListToDoubleArr(errDataFiltered);
        int length = yDataFiltered.size();
        var xData= ArrayCreator.createArrayFromStartAndEndWithNofItems(0d, length-1.0, length);
        creator.addErrorBand(measure, xData, yDataFilteredArr, errDataArr, Color.BLACK);
        SwingUtilities.invokeLater(() -> creator.create().setVisible(true));
    }


    private static List<Double> filter(List<Double> inList, int lengthWindow) {
        MovingAverage movingAverage = new MovingAverage(lengthWindow, inList);
        return movingAverage.getFiltered();
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
