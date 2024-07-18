package maze_domain_design.domain.shared;

import lombok.AllArgsConstructor;
import maze_domain_design.domain.trainer.Trainer;
import maze_domain_design.domain.trainer.aggregates.Recorder;
import maze_domain_design.services.TrainingService;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        var sumRewardsList = getSumRewardsList(recorder);
        var pRandActionList = getPRandActionList(recorder);
        XYChart chart0 = getChart("sumRewards", sumRewardsList);
        XYChart chart1 = getChart("pRandAction", pRandActionList);
        charts.add(chart0);
        charts.add(chart1);
        new SwingWrapper<>(charts).displayChartMatrix();
    }

    List<Double> getSumRewardsList(Recorder recorder) {
        return recorder.getIds().stream()
                .map(id -> recorder.getExp(id).getSumRewards()).toList();
    }

    List<Double> getPRandActionList(Recorder recorder) {
        return recorder.getIds().stream()
                .map(id -> recorder.getExp(id).getPRandomAction()).toList();
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
