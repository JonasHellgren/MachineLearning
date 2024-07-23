package maze_domain_design.domain.shared;

import lombok.AllArgsConstructor;
import maze_domain_design.domain.trainer.Trainer;
import maze_domain_design.domain.trainer.aggregates.Recorder;
import maze_domain_design.domain.trainer.value_objects.EpisodeInfoForRecording;
import maze_domain_design.services.TrainingService;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.ToDoubleFunction;

@AllArgsConstructor
public class TrainerPlotter {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 200;
    public static final String X_AXIS_TITLE = "episode";
    Trainer trainer;

    public static TrainerPlotter ofTrainingService(TrainingService trainingService) {
        return new TrainerPlotter(trainingService.getTrainer());
    }

    public void plot() {
        List<XYChart> charts = new ArrayList<>();
        var recorder = trainer.getMediator().getRecorder();
        var pRandActionList = getListWithInfoValues(recorder, ei -> ei.pRandomAction() );
        var tdErrorList = getListWithInfoValues(recorder, ei -> ei.tdErrorAvg() );
        charts.add(getChart("sumRewards", getSumRewardsList(recorder)));
        charts.add(getChart("pRandAction", pRandActionList));
        charts.add(getChart("tdError", tdErrorList));
        charts.add(getChart("nSTeps", getNStepsList(recorder)));
        new SwingWrapper<>(charts).displayChartMatrix();
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

    XYChart getChart(String sumRewards, List<Double> valList) {
        var recorder = trainer.getMediator().getRecorder();
        var episodeList = recorder.getIds();
        XYChart chart = new XYChartBuilder()
                .xAxisTitle(X_AXIS_TITLE).yAxisTitle(sumRewards)
                .width(WIDTH).height(HEIGHT).build();
        chart.getStyler().setYAxisMin(Collections.min(valList));
        chart.getStyler().setYAxisMax(Collections.max(valList));
        chart.getStyler().setLegendVisible(false);
        XYSeries series = chart.addSeries(sumRewards, episodeList, valList);
        series.setMarker(SeriesMarkers.NONE);
        return chart;
    }
}
