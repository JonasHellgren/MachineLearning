package domain_design_tabular_q_learning.domain.plotting;

import common.math.MovingAverage;
import lombok.AllArgsConstructor;
import domain_design_tabular_q_learning.domain.trainer.Trainer;
import domain_design_tabular_q_learning.domain.trainer.aggregates.Recorder;
import domain_design_tabular_q_learning.domain.trainer.value_objects.EpisodeInfoForRecording;
import domain_design_tabular_q_learning.services.TrainingService;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.ToDoubleFunction;

import static org.knowm.xchart.BitmapEncoder.*;

@AllArgsConstructor
public class TrainerPlotter<V,A,P> {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 200;
    public static final String X_AXIS_TITLE = "Episode";
    public static final int LENGTH_WINDOW_R = 10;
    public static final int LENGTH_WINDOW_TD = 10;
    Trainer<V,A,P> trainer;

    public static <V,A,P> TrainerPlotter<V,A,P> ofTrainingService(
            TrainingService<V,A,P> trainingService) {
        return new TrainerPlotter<>(trainingService.getTrainer());
    }

    public void plot() {
        List<XYChart> charts = getCharts();
        new SwingWrapper<>(charts).displayChartMatrix();
    }

     List<XYChart> getCharts() {
        List<XYChart> charts = new ArrayList<>();
        var recorder = trainer.getMediator().getRecorder();
        var pRandActionList = getListWithInfoValues(recorder, ei -> ei.pRandomAction() );
        var tdErrorList = getListWithInfoValues(recorder, ei -> ei.tdErrorAvg() );
        charts.add(getChart("Accumulated rewards", filter(getSumRewardsList(recorder), LENGTH_WINDOW_R)));
        charts.add(getChart("Prob. rand. action", pRandActionList));
        charts.add(getChart("Temporal diff. error", filter(tdErrorList, LENGTH_WINDOW_TD)));
        charts.add(getChart("Number of steps", getNStepsList(recorder)));
        charts.forEach(c -> reduceXAxisTicksClutter(c,100));
        return charts;
    }

    List<Double> getSumRewardsList(Recorder recorder) {
        return recorder.getIds().stream()
                .map(id -> recorder.getExp(id).getSumRewards()).toList();

    }List<Double> getNStepsList(Recorder recorder) {
        return recorder.getIds().stream()
                .map(id -> (double) recorder.getExp(id).getNSteps()).toList();
    }

    List<Double> getListWithInfoValues(Recorder recorder, ToDoubleFunction<EpisodeInfoForRecording> fcn) {
        return recorder.getIds().stream()
                .map(id -> fcn.applyAsDouble(recorder.getExp(id).getEpisodeInfoForRecording())).toList();
    }

    XYChart getChart(String name, List<Double> valList) {
        var recorder = trainer.getMediator().getRecorder();
        var episodeList = recorder.getIds();
        XYChart chart = new XYChartBuilder()
                .xAxisTitle(X_AXIS_TITLE).yAxisTitle(name)
                .width(WIDTH).height(HEIGHT).build();
        chart.getStyler().setYAxisMin(Collections.min(valList));
        chart.getStyler().setYAxisMax(Collections.max(valList));
        chart.getStyler().setLegendVisible(false);
        XYSeries series = chart.addSeries(name, episodeList, valList);
        series.setMarker(SeriesMarkers.NONE);
        return chart;
    }

    public void saveCharts(FileDirName file) throws IOException {
        for (XYChart c:getCharts()) {
            saveBitmap(
                    c,
                    file.dir()+file.fileName()+c.getYAxisTitle()+file.fileEnd(),
                    BitmapFormat.PNG);
        }
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

    private List<Double> filter(List<Double> inList, int lengthWindow) {
        MovingAverage movingAverage = new MovingAverage(lengthWindow, inList);
        return movingAverage.getFiltered();
    }
}
