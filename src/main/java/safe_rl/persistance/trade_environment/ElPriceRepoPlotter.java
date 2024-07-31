package safe_rl.persistance.trade_environment;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.With;
import org.knowm.xchart.*;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
public class ElPriceRepoPlotter {

    public static final Color MARKER_COLOR_ADDED = Color.RED;

    @Builder
    @With
    public record  Settings(
            Boolean isLegend,
            Integer markerSize) {
        public static  Settings newDefault() {
            return Settings.builder().isLegend(true).markerSize(5).build();
        }
    }

    public static final int WIDTH = 400;
    public static final int HEIGHT = 200;
    ElPriceRepo repo;
    Settings settings;

    static Function<String,String> putParAround = s -> "("+s+")";

    public static ElPriceRepoPlotter of(ElPriceRepo repo) {
        return new ElPriceRepoPlotter(repo,Settings.newDefault());
    }

    public static ElPriceRepoPlotter withSettings(ElPriceRepo repo, Settings settings) {
        return new ElPriceRepoPlotter(repo,settings);
    }

    public void plotTrajectories(ElType type) {
        checkOkRepo();
        var informer=new RepoInformer(repo);
        var chart = createChart(
                type.toString()+informer.uniqueRegions(type),
                "Time"+putParAround.apply("h"),
                "Price"+putParAround.apply(type.unit));
        styleLineChart(chart);
        setYAxisRange(chart, informer.minPriceAllDays(type), informer.maxPriceAllDays(type));
        fillChartWithPriceData(type, chart);
        new SwingWrapper<>(chart).displayChart();
    }

    public void plotScatter() {
        XYChart chart = getScatterChartDay();
        new SwingWrapper<>(chart).displayChart();
    }


    public void plotScatterWithAddedPoints(List<Double> avgList, List<Double> stdList) {
        Preconditions.checkArgument(!avgList.isEmpty() && avgList.size()==stdList.size(),"Non correct list(s)");
        XYChart chart = getScatterChartDay();
        var series=chart.addSeries("Close to cluster", avgList, stdList);
        series.setMarkerColor(MARKER_COLOR_ADDED);
        new SwingWrapper<>(chart).displayChart();
    }

    XYChart getScatterChartDay() {
        checkOkRepo();
        var informer=new RepoInformer(repo);
        var chart = createChart(
                "Scatter",
                "Avg. FCR"+putParAround.apply(ElType.FCR.unit),
                "Std. Energy"+putParAround.apply(ElType.ENERGY.unit));
        styleScatterChart(chart);
        var avgList=informer.averagePriceEachDay(ElType.FCR);
        var stdList=informer.stdPriceEachDay(ElType.ENERGY);
        chart.addSeries("Days", avgList, stdList);
        return chart;
    }


    private static XYChart createChart(String title, String xAxisTitle, String yAxisTitle) {
        return new XYChartBuilder()
                .title(title)
                .xAxisTitle(xAxisTitle).yAxisTitle(yAxisTitle)
                .width(WIDTH).height(HEIGHT)
                .build();
    }

    private void fillChartWithPriceData(ElType type, XYChart chart) {
        for (DayId id:repo.idsAll()) {
            var priceData=repo.getPriceDataForDay(id, type);
            XYSeries series = chart.addSeries(id.toDateString(), null, priceData.pricesAllHours());
            series.setMarker(SeriesMarkers.NONE);
        }
    }

    private  void styleLineChart(XYChart chart) {
        XYStyler styler = chart.getStyler();
        styler.setPlotGridLinesVisible(true);
        styler.setLegendVisible(settings.isLegend);
    }


    private void styleScatterChart(XYChart chart) {
        XYStyler styler = chart.getStyler();
        styler.setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        styler.setLegendVisible(settings.isLegend);
        styler.setMarkerSize(settings.markerSize);
    }


    private void setYAxisRange(XYChart chart, Double minPrice, Double maxPrice) {
        XYStyler styler = chart.getStyler();
        styler.setYAxisMin(minPrice);
        styler.setYAxisMax(maxPrice);
    }


    private void checkOkRepo() {
        Preconditions.checkArgument(repo.checkIsOk(),
                "Non correct repo, may for ex be different size of data bases");
    }

}
