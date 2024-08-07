package safe_rl.environments.trading_electricity;

import com.beust.jcommander.internal.Lists;
import common.list_arrays.ListUtils;
import common.other.NumberFormatterUtil;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;
import safe_rl.domain.simulator.value_objects.SimulationResult;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.function.Function;

/***
 * simulationResultsMap has one simulation per key
 */

import static org.knowm.xchart.BitmapEncoder.saveBitmapWithDPI;

public class TradeSimulationPlotter<V> {
    public static final String AXIS_TITLE = "Time (h)";
    public static final int WIDTH = 250;
    public static final int HEIGHT = 200;
    public static final BitmapEncoder.BitmapFormat FORMAT = BitmapEncoder.BitmapFormat.PNG;
    public static final int DPI = 300;

    SettingsTrading settings;
    RewardAndConstraintEvaluator evaluator;
    TradeStateUpdater stateUpdater;


    public TradeSimulationPlotter(SettingsTrading settings) {
        this.settings = settings;
        this.evaluator=new RewardAndConstraintEvaluator(settings);
        this.stateUpdater=new TradeStateUpdater(settings);
    }

    public void plot(Map<Integer, List<SimulationResult<V>>> simulationResultsMap, double valueInStartState) {
        List<XYChart> charts = Lists.newArrayList();
        Function<SimulationResult<V>, Double> extractorRev = sr -> sr.reward();

        addPowerChart(simulationResultsMap, charts);
        addPowerCapChart(simulationResultsMap, charts);
        addPowerChangeChart(simulationResultsMap, charts);
        addSocChart(simulationResultsMap, charts);
        addRevenueChart(simulationResultsMap, charts, extractorRev);
        addAccRevChart(simulationResultsMap, charts, extractorRev,valueInStartState);
        addCostSoHChart(simulationResultsMap, charts);
        addRevEnergy(simulationResultsMap, charts);
        addRevFCR(simulationResultsMap, charts);
        styleCharts(charts);
        new SwingWrapper<>(charts).displayChartMatrix();
    }



    @SneakyThrows
    public void savePlots(Map<Integer, List<SimulationResult<V>>> simulationResultsMap, String path, String caseName) {
        List<XYChart> charts = Lists.newArrayList();
        addPowerChart(simulationResultsMap, charts);
        addSocChart(simulationResultsMap, charts);
        styleCharts(charts);
        saveBitmapWithDPI(charts.get(0), getFileName(path, "power", caseName), FORMAT, DPI);
        saveBitmapWithDPI(charts.get(1), getFileName(path, "soc", caseName), FORMAT, DPI);
    }

    @NotNull
    private static String getFileName(String path, String measureName, String caseName) {
        return path + "/" + measureName + "_"+ caseName;
    }

    private static void styleCharts(List<XYChart> charts) {
        charts.forEach(c -> c.getSeriesMap().values().forEach(s -> s.setMarker(SeriesMarkers.NONE)));
        charts.forEach(c -> c.getStyler().setChartBackgroundColor(Color.WHITE));
        charts.forEach(c -> c.getStyler().setPlotBorderVisible(false)); // Border
        charts.forEach(c -> c.getStyler().setLegendVisible(false));
    }


    private  void addRevFCR(Map<Integer, List<SimulationResult<V>>> simulationResultsMap, List<XYChart> charts) {
        Function<SimulationResult<V>, Double> extractor = sr -> {
            double a=sr.actionCorrected().asDouble();
            StateTrading state = (StateTrading) sr.state();
            return evaluator.calculateIncomes(state,a,0d).incomeFcr();
        };
        List<Double> allValues = getAllValues(simulationResultsMap, extractor);
        XYChart chartAction = new XYChartBuilder()
                .xAxisTitle(AXIS_TITLE).yAxisTitle("Inc. FCR (Euro/h)").width(WIDTH).height(HEIGHT).build();
        setYMinMax(chartAction, Collections.min(allValues), Collections.max(allValues));
        addDataToChart(simulationResultsMap, chartAction, extractor);
        charts.add(chartAction);
    }


    private void addRevEnergy(Map<Integer, List<SimulationResult<V>>> simulationResultsMap, List<XYChart> charts) {
        Function<SimulationResult<V>, Double> extractor = sr -> {
            double a=sr.actionCorrected().asDouble();
            StateTrading state = (StateTrading) sr.state();
            return evaluator.calculateIncomes(state,a,0d).incomeEnergy();
        };
        List<Double> allValues = getAllValues(simulationResultsMap, extractor);
        XYChart chartAction = new XYChartBuilder()
                .xAxisTitle(AXIS_TITLE).yAxisTitle("Inc. energy (Euro/h)").width(WIDTH).height(HEIGHT).build();
        setYMinMax(chartAction, Collections.min(allValues), Collections.max(allValues));
        addDataToChart(simulationResultsMap, chartAction, extractor);
        charts.add(chartAction);

    }

    private void addCostSoHChart(Map<Integer, List<SimulationResult<V>>> simulationResultsMap, List<XYChart> charts) {
        Function<SimulationResult<V>, Double> extractor = sr -> {
            double a=sr.actionCorrected().asDouble();
            StateTrading state = (StateTrading) sr.state();
            var updaterRes=stateUpdater.update(state,0,a);
            return evaluator.calculateIncomes(state,a,updaterRes.dSoh()).costDegradation();
        };
        List<Double> allValues = getAllValues(simulationResultsMap, extractor);
        XYChart chartAction = new XYChartBuilder()
                .xAxisTitle(AXIS_TITLE).yAxisTitle("Cost. degradation (Euro/h)").width(WIDTH).height(HEIGHT).build();
        setYMinMax(chartAction, Collections.min(allValues), Collections.max(allValues));
        addDataToChart(simulationResultsMap, chartAction, extractor);
        charts.add(chartAction);
    }

    private void addAccRevChart(Map<Integer, List<SimulationResult<V>>> simulationResultsMap,
                                List<XYChart> charts,
                                Function<SimulationResult<V>, Double> extractorRev,
                                double valueInStartState) {
        DecimalFormat formatter=NumberFormatterUtil.formatterTwoDigits;
        XYChart chartAccumRev= getXyChart(
                "Start state value (Euro)="+formatter.format(valueInStartState),
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

    private void addPowerChart(Map<Integer, List<SimulationResult<V>>> simulationResultsMap, List<XYChart> charts) {
        Function<SimulationResult<V>, Double> extractor = sr -> sr.actionCorrected().asDouble();
        List<Double> allValues = getAllValues(simulationResultsMap, extractor);
        XYChart chartAction = new XYChartBuilder()
                .xAxisTitle(AXIS_TITLE).yAxisTitle("Power (kW)").width(WIDTH).height(HEIGHT).build();
        setYMinMax(chartAction, Collections.min(allValues), Collections.max(allValues));
        addDataToChart(simulationResultsMap, chartAction, extractor);
        charts.add(chartAction);
    }

    private void addSocChart(Map<Integer, List<SimulationResult<V>>> simulationResultsMap, List<XYChart> charts) {
        XYChart chartSoc = getXyChart("","Soc");
        setYMinMax(chartSoc, 0, 1);
        Function<SimulationResult<V>, Double> extractorSoc = sr ->
                sr.state().continuousFeatures()[StateTrading.INDEX_SOC];
        addDataToChart(simulationResultsMap, chartSoc, extractorSoc);
        charts.add(chartSoc);
    }

    private void addPowerCapChart(Map<Integer, List<SimulationResult<V>>> simulationResultsMap, List<XYChart> charts) {
        var chart = getXyChart("","PC");
        var range = settings.powerCapacityFcrRange();
        setYMinMax(chart, range.lowerEndpoint(), range.upperEndpoint());
        Function<SimulationResult<V>, Double> extractorPC = sr ->
                settings.powerCapacityFcr(sr.state().continuousFeatures()[StateTrading.INDEX_SOC]);
        addDataToChart(simulationResultsMap, chart, extractorPC);
        charts.add(chart);

    }


    private void addPowerChangeChart(Map<Integer, List<SimulationResult<V>>> simulationResultsMap, List<XYChart> charts) {
        XYChart chartSoc = getXyChart("","Power change (kW)");
        setYMinMax(chartSoc, 0, settings.powerChargeMax());
        Function<SimulationResult<V>, Double> extractorSoc = SimulationResult::getActionChange;
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


    private void addRevenueChart(Map<Integer, List<SimulationResult<V>>> simulationResultsMap,
                                 List<XYChart> charts,
                                 Function<SimulationResult<V>, Double> extractorRev) {
        List<Double> allValues = getAllValues(simulationResultsMap, extractorRev);
        XYChart chartRev= getXyChart("","Revenue (Euro)");
        setYMinMax(chartRev, Collections.min(allValues), Collections.max(allValues));
        addDataToChart(simulationResultsMap, chartRev, extractorRev);
        charts.add(chartRev);
    }


    List<Double> getAllValues(Map<Integer, List<SimulationResult<V>>> simulationResultsMap,
                              Function<SimulationResult<V>, Double> extractorRev) {
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
            XYSeries series = addValuesToChart(chart, values, entry.getKey());
            series.setMarker(SeriesMarkers.NONE);
        }
    }

    private  XYSeries addValuesToChart(XYChart chart, List<Double> values, Integer simNr) {
        return chart.addSeries("" + simNr, getTimes(values), values);
    }

    private List<Double> getTimes(List<Double> yList) {
        return ListUtils.doublesStartEndStep(0, settings.dt() * (yList.size() - 1), settings.dt());
    }


}
