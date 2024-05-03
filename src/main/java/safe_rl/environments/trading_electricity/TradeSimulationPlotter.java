package safe_rl.environments.trading_electricity;

import com.beust.jcommander.internal.Lists;
import common.list_arrays.ListUtils;
import common.other.NumberFormatterUtil;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;
import safe_rl.domain.value_classes.SimulationResult;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;

@AllArgsConstructor
public class TradeSimulationPlotter<V> {
    public static final String AXIS_TITLE = "Time";
    public static final int WIDTH = 400;
    public static final int HEIGHT = 200;

    SettingsTrading settings;


    public void plot(Map<Integer, List<SimulationResult<V>>> simulationResultsMap, double valueInStartState) {
        List<XYChart> charts = new ArrayList<>();
        addActionChart(simulationResultsMap, charts);
        addSocChart(simulationResultsMap, charts);
        Function<SimulationResult<V>, Double> extractorRev = sr -> sr.reward();
        addAccRevChart(simulationResultsMap, charts, extractorRev,valueInStartState);
        addRevenueChart(simulationResultsMap, charts, extractorRev);
        addSohChart(simulationResultsMap, charts);
        addAccRevFCR(simulationResultsMap, charts);
        new SwingWrapper<>(charts).displayChartMatrix();
    }

    private  void addAccRevFCR(Map<Integer, List<SimulationResult<V>>> simulationResultsMap, List<XYChart> charts) {
        XYChart chartAccumRevFcr= getXyChart("","Acc rev. FCR");
        for (Map.Entry<Integer, List<SimulationResult<V>>> entry : simulationResultsMap.entrySet()) {
            List<Double> revFcrList = ListUtils.createListWithEqualElementValues(
                    settings.nTimeSteps(),settings.revFCRPerTimeStep());
            XYSeries series = chartAccumRevFcr.addSeries(
                    "" + entry.getKey(),
                    getTimes(revFcrList),
                    ListUtils.cumulativeSum(revFcrList));
            series.setMarker(SeriesMarkers.NONE);
        }
        charts.add(chartAccumRevFcr);
    }

    private void addSohChart(Map<Integer, List<SimulationResult<V>>> simulationResultsMap, List<XYChart> charts) {
        Function<SimulationResult<V>, Double> extractorCostDsoh = sr -> {
            StateTrading stateCasted = (StateTrading) sr.state();
            double dSoh = 1 - stateCasted.soh();
            return dSoh * settings.priceBattery();
        };
        var allValuesCostSoh = getAllValues(simulationResultsMap, extractorCostDsoh);
        DecimalFormat formatter=NumberFormatterUtil.formatterTwoDigits;
        double dSohMaxInPercentage = Collections.max(allValuesCostSoh) / settings.priceBattery()*100;
        XYChart chartdSoh= getXyChart("dSoh(%)="+ formatter.format(dSohMaxInPercentage),"cost soh (Euro)");
        setYMinMax(chartdSoh, Collections.min(allValuesCostSoh), Collections.max(allValuesCostSoh));
        addDataToChart(simulationResultsMap, chartdSoh, extractorCostDsoh);
        charts.add(chartdSoh);
    }

    private void addAccRevChart(Map<Integer, List<SimulationResult<V>>> simulationResultsMap,
                                List<XYChart> charts,
                                Function<SimulationResult<V>, Double> extractorRev,
                                double valueInStartState) {
        DecimalFormat formatter=NumberFormatterUtil.formatterTwoDigits;
        XYChart chartAccumRev= getXyChart(
                "valueInStartState(Euro)="+formatter.format(valueInStartState),
                "Acc revenue (Euro)");
        for (Map.Entry<Integer, List<SimulationResult<V>>> entry : simulationResultsMap.entrySet()) {
            List<Double> revenues = entry.getValue().stream().map(extractorRev).toList();
            XYSeries series = chartAccumRev.addSeries(
                    "" + entry.getKey(),
                    getTimes(revenues),
                    ListUtils.cumulativeSum(revenues));
            series.setMarker(SeriesMarkers.NONE);
        }
        charts.add(chartAccumRev);
    }

    private void addRevenueChart(Map<Integer, List<SimulationResult<V>>> simulationResultsMap, List<XYChart> charts, Function<SimulationResult<V>, Double> extractorRev) {
        List<Double> allValues = getAllValues(simulationResultsMap, extractorRev);
        XYChart chartRev= getXyChart("","Revenue (Euro)");
        setYMinMax(chartRev, Collections.min(allValues), Collections.max(allValues));
        addDataToChart(simulationResultsMap, chartRev, extractorRev);
        charts.add(chartRev);
    }

    private void addSocChart(Map<Integer, List<SimulationResult<V>>> simulationResultsMap, List<XYChart> charts) {
        XYChart chartSoc = getXyChart("","Soc");
        setYMinMax(chartSoc, 0, 1);
        Function<SimulationResult<V>, Double> extractorSoc = sr ->
                sr.state().continousFeatures()[StateTrading.INDEX_SOC];
        addDataToChart(simulationResultsMap, chartSoc, extractorSoc);
        charts.add(chartSoc);
    }

    private static XYChart getXyChart(String title,String titleAxis) {
        XYChart chart = new XYChartBuilder()
                .title(title).xAxisTitle(AXIS_TITLE).yAxisTitle(titleAxis)
                .width(WIDTH).height(HEIGHT).build();
        chart.getStyler().setYAxisDecimalPattern("#.####"); // Set Y-axis to display numbers with 2 decimal places
        return chart;
    }

    private void addActionChart(Map<Integer, List<SimulationResult<V>>> simulationResultsMap, List<XYChart> charts) {
        XYChart chartAction = new XYChartBuilder()
                .xAxisTitle(AXIS_TITLE).yAxisTitle("Power (kW)").width(WIDTH).height(HEIGHT).build();
        setYMinMax(chartAction, -settings.powerBattMax(), settings.powerBattMax());
        Function<SimulationResult<V>, Double> extractor = sr -> sr.action().asDouble();
        addDataToChart(simulationResultsMap, chartAction, extractor);
        charts.add(chartAction);
    }

    List<Double> getAllValues(Map<Integer, List<SimulationResult<V>>> simulationResultsMap, Function<SimulationResult<V>, Double> extractorRev) {
        List<Double> allValues = Lists.newArrayList();
        for (Map.Entry<Integer, List<SimulationResult<V>>> entry : simulationResultsMap.entrySet()) {
            List<Double> values = entry.getValue().stream().map(extractorRev).toList();
            allValues.addAll(values);
        }
        return allValues;
    }

    private void setYMinMax(XYChart chart, double yAxisMin, double yAxisMax) {
        chart.getStyler().setYAxisMin(yAxisMin);
        chart.getStyler().setYAxisMax(yAxisMax);
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
