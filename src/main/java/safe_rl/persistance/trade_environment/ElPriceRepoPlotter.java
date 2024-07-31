package safe_rl.persistance.trade_environment;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.markers.SeriesMarkers;

@AllArgsConstructor
public class ElPriceRepoPlotter {

    public static final int WIDTH = 400;
    public static final int HEIGHT = 300;
    ElPriceRepo repo;

    public void plotTrajectories(ElType type) {
        Preconditions.checkArgument(repo.checkIsOk(),
                "Non correct repo, may for ex be different size of data bases");
        var informer=new RepoInformer(repo);

        XYChart chart = new XYChartBuilder()
                .title(type.toString())
                .xAxisTitle("Time (h)").yAxisTitle("Price")
                .width(WIDTH).height(HEIGHT)
                .build();
        XYStyler styler = chart.getStyler();
        styler.setYAxisMin(informer.minPriceAllDays(type));
        styler.setYAxisMax(informer.maxPriceAllDays(type));
        styler.setPlotGridLinesVisible(true);

        System.out.println("informer.maxPriceAllDays(type) = " + informer.maxPriceAllDays(type));

        for (DayId id:repo.idsAll()) {
            var priceData=repo.getPriceDataForDay(id,type);
            System.out.println("priceData.pricesAllHours() = " + priceData.pricesAllHours());
            XYSeries series = chart.addSeries(id.toDateString(), null, priceData.pricesAllHours());
            series.setMarker(SeriesMarkers.NONE);

        }

        var sw= new SwingWrapper<>(chart).displayChart();


    }

}
