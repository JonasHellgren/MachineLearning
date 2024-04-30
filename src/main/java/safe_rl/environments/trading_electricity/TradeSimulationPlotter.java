package safe_rl.environments.trading_electricity;

import com.beust.jcommander.internal.Lists;
import common.list_arrays.ListUtils;
import lombok.AllArgsConstructor;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;
import safe_rl.domain.value_classes.SimulationResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@AllArgsConstructor
public class TradeSimulationPlotter<V> {


    public static final String AXIS_TITLE = "Time";
    public static final int WIDTH = 400;
    public static final int HEIGHT = 200;

    SettingsTrading settings;

    public void plotMany() {

    }

    public void plot(Map<Integer, List<SimulationResult<V>>> simulationResultsMap) {
        int numCharts = 4;
        int nofLines = simulationResultsMap.size();
        List<XYChart> charts = new ArrayList<>();

        double dt = 1; //todo


        XYChart chartAction = new XYChartBuilder()
                .xAxisTitle(AXIS_TITLE).yAxisTitle("action").width(WIDTH).height(HEIGHT).build();
        setYMinMax(chartAction, -settings.powerBattMax(), settings.powerBattMax());
        Function<SimulationResult<V>, Double> extractor = sr -> sr.action().asDouble();
        addDataToChart(simulationResultsMap, chartAction, extractor);
        charts.add(chartAction);

        XYChart chartSoc = new XYChartBuilder()
                .xAxisTitle(AXIS_TITLE).yAxisTitle("Soc").width(WIDTH).height(HEIGHT).build();
        setYMinMax(chartSoc, 0, 1);
        Function<SimulationResult<V>, Double> extractorSoc = sr ->
                sr.state().continousFeatures()[StateTrading.INDEX_SOC];
        addDataToChart(simulationResultsMap, chartSoc, extractorSoc);
        charts.add(chartSoc);

        Function<SimulationResult<V>, Double> extractorRev = sr -> sr.reward();
        List<Double> allValues = Lists.newArrayList();
        for (Map.Entry<Integer, List<SimulationResult<V>>> entry : simulationResultsMap.entrySet()) {
            List<Double> values = entry.getValue().stream().map(extractorRev).toList();
            allValues.addAll(values);
        }

        XYChart chartRev = new XYChartBuilder()
                .xAxisTitle(AXIS_TITLE).yAxisTitle("Revenue").width(WIDTH).height(HEIGHT).build();
        setYMinMax(chartRev, Collections.min(allValues), Collections.max(allValues));
        addDataToChart(simulationResultsMap, chartRev, extractorRev);
        charts.add(chartRev);

        XYChart chartAccumRev = new XYChartBuilder()
                .xAxisTitle(AXIS_TITLE).yAxisTitle("Acc revenue").width(WIDTH).height(HEIGHT).build();

        for (Map.Entry<Integer, List<SimulationResult<V>>> entry : simulationResultsMap.entrySet()) {
            List<Double> revenues = entry.getValue().stream().map(extractorRev).toList();
            XYSeries series = chartAccumRev.addSeries(
                    "" + entry.getKey(),
                    getTimes(revenues),
                    ListUtils.cumulativeSum(revenues));
            series.setMarker(SeriesMarkers.NONE);
        }
        charts.add(chartAccumRev);


        new SwingWrapper<>(charts).displayChartMatrix();

    }

    private void setYMinMax(XYChart chartAction, double yAxisMin, double yAxisMax) {
        chartAction.getStyler().setYAxisMin(yAxisMin);
        chartAction.getStyler().setYAxisMax(yAxisMax);
    }

    private void addDataToChart(Map<Integer, List<SimulationResult<V>>> simulationResultsMap,
                                XYChart chart,
                                Function<SimulationResult<V>, Double> extractor) {
        for (Map.Entry<Integer, List<SimulationResult<V>>> entry : simulationResultsMap.entrySet()) {
            List<Double> values = entry.getValue().stream().map(extractor).toList();
            XYSeries series = chart.addSeries("" + entry.getKey(), getTimes(values), values);
            series.setMarker(SeriesMarkers.NONE);
        }
    }

    private List<Double> getTimes(List<Double> yList) {
        return ListUtils.doublesStartEndStep(0, settings.dt() * (yList.size() - 1), settings.dt());
    }



}
