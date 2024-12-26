package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.trainer.TrainerI;
import book_rl_explained.lunar_lander.domain.trainer.TrainerParameters;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.hellgren.plotters.plotting_2d.ErrorBandCreator;
import org.hellgren.utilities.list_arrays.ArrayCreator;
import org.hellgren.utilities.list_arrays.List2ArrayConverter;
import org.hellgren.utilities.math.MovingAverage;
import org.knowm.xchart.XYChart;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

import static org.hellgren.utilities.list_arrays.MyListUtils.elementSubtraction;
import static org.hellgren.utilities.list_arrays.MyListUtils.everyItemAbsolute;

@Log
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlotterProgressMeasures {

    public static final String EPISODE = "Episode";

    record ErrorBandData(List<Double> yData0) {
        int length() { return yData0.size(); }
        List<Double> yDataFiltered() { return filter(yData0,length()/ N_WINDOWS); }
        List<Double> errData0() { return elementSubtraction(yDataFiltered(), yData0); }
        List<Double> errData() { return everyItemAbsolute(errData0()); }
        List<Double> errDataFiltered() { return filter(errData(),length()/ (N_WINDOWS)); }
        double[] errDataFilteredAsArray() { return List2ArrayConverter.convertListToDoubleArr(errDataFiltered()); }
        double[] yDataFilteredAsArray() { return List2ArrayConverter.convertListToDoubleArr(yDataFiltered()); }
        double[] xDataAsArray() { return ArrayCreator.createArrayFromStartAndEndWithNofItems(0d, length()-1.0, length()); }
    }

    public static final int N_WINDOWS = 10;
    public static final int WIDTH = 200;
    public static final int HEIGHT = 150;

    RecorderTrainingProgress recorder;

    public static PlotterProgressMeasures of(RecorderTrainingProgress recorder,
                                             TrainerParameters trainerParameters) {
        return new PlotterProgressMeasures(recorder);
    }

    public static PlotterProgressMeasures of(TrainerI trainer) {
        return new PlotterProgressMeasures(
                trainer.getRecorder()
);
    }

    public void plot() {
        Preconditions.checkArgument(!recorder.isEmpty(), "No training progress data to plot");
        var measures=List.of("sumRewards","stdActor");
        showOneMeasurePerPlot(measures);
        measures=List.of("tdError","tdErrorClipped");
        showTwoMeasuresInOnePlot(measures, "tdError");
        measures=List.of("gradMean","gradMeanClipped");
        showTwoMeasuresInOnePlot(measures, "gradMean");
    }

    private void showOneMeasurePerPlot(List<String> measures) {
        for (String measure : measures) {
            var errorBandData= new ErrorBandData(recorder.trajectory(measure));
            showPlotNew(measure, errorBandData);
        }
    }

    private void showTwoMeasuresInOnePlot(List<String> measures, String yAxisLabel) {
        var settings = ErrorBandCreator.Settings.ofDefaults()
                .withTitle("").withYAxisLabel(yAxisLabel).withXAxisLabel(EPISODE).withShowLegend(false);
        var creator = ErrorBandCreator.newOfSettings(settings);
        for (String measure : measures) {
            var errorBandData= new ErrorBandData(recorder.trajectory(measure));
            addErrorBand(measure, creator, errorBandData);
        }
        SwingUtilities.invokeLater(() -> creator.create().setVisible(true));
    }

    private static void addErrorBand(String measure, ErrorBandCreator creator, ErrorBandData errorBandData) {
        creator.addErrorBand(measure,
                errorBandData.xDataAsArray(),
                errorBandData.yDataFilteredAsArray(),
                errorBandData.errDataFilteredAsArray(),
                Color.BLACK);
    }

    private static void showPlotNew(String measure, ErrorBandData errorBandData) {
        var settings = ErrorBandCreator.Settings.ofDefaults()
                .withTitle(measure).withYAxisLabel(measure).withXAxisLabel(EPISODE).withShowLegend(false);
        var creator = ErrorBandCreator.newOfSettings(settings);
        addErrorBand(measure, creator, errorBandData);
        SwingUtilities.invokeLater(() -> creator.create().setVisible(true));
    }

    private static List<Double> filter(List<Double> inList, int lengthWindow) {
        MovingAverage movingAverage = new MovingAverage(lengthWindow, inList);
        return movingAverage.getFiltered();
    }


}
