package safe_rl.environments.trading_electricity;

import common.list_arrays.ListUtils;
import lombok.Builder;
import org.apache.commons.math3.util.Pair;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import safe_rl.persistance.trade_environment.DayId;

import java.util.ArrayList;
import java.util.List;

import static safe_rl.other.runner_helpers.XticksSetter.getXTicks;
import static safe_rl.other.runner_helpers.XticksSetter.setXTicks;

@Builder
public class ElectricPricePlotter {

    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;
    DayId dayId;
    Pair<Integer, Integer> fromToHour;
    Pair<List<Double>, List<Double>> energyFcrPricePair;
    SettingsTrading settings;

    public void plot() {
        List<XYChart> charts = new ArrayList<>();
        var pricesEnergy = energyFcrPricePair.getFirst();
        var pricesFcr = energyFcrPricePair.getSecond();
        charts.add(getChart(pricesEnergy, "Energy (E/kWh)"));
        charts.add(getChart(pricesFcr, "FCR E/kW)"));
        new SwingWrapper<>(charts).displayChartMatrix();
    }

    XYChart getChart(List<Double> prices, String title) {
        XYChart chart = new XYChartBuilder()
                .xAxisTitle("Time (h)").yAxisTitle("Price").width(WIDTH).height(HEIGHT).build();
        chart.getStyler().setYAxisMin(ListUtils.findMin(prices).orElseThrow());
        chart.getStyler().setYAxisMax(ListUtils.findMax(prices).orElseThrow());
        chart.setTitle(title + ", day=" + dayId.toDateString());
        chart.addSeries(title, getXTicks(prices, settings), prices);
        chart.getStyler().setLegendVisible(false);
        setXTicks(chart, fromToHour);
        return chart;
    }

}
