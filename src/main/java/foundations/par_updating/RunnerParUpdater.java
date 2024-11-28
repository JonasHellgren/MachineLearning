package foundations.par_updating;

import org.apache.commons.math3.util.Pair;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.List;

public class RunnerParUpdater {

    public static final int M_START = 0;
    public static final int N_UPDATES = 50;
    static List<Double> learningRateList = List.of(0.1, 0.2, 0.3, 0.4);
    static double M_REF=9;

    public static void main(String[] args) {
        int nofLines = 4;
        var chart = new XYChartBuilder().xAxisTitle("Iteration").yAxisTitle("m").width(600).height(200).build();
        styleChart(chart);

        for (int i = M_START; i < nofLines; i++) {
            Double lr = learningRateList.get(i);
            var xyListPair= doUpdating(lr);
            XYSeries series = chart.addSeries("" + lr, xyListPair.getFirst(),xyListPair.getSecond());
            series.setMarker(SeriesMarkers.NONE);
        }
        new SwingWrapper<>(chart).displayChart();
    }

    private static Pair<List<Double>,List<Double>> doUpdating(Double lr) {

        double m= M_START;
        List<Double> iList= new ArrayList<>();
        List<Double> mList= new ArrayList<>();
        for (int i = 0; i < N_UPDATES; i++) {
            iList.add((double) i);
            mList.add(m);
            m=m+lr*(M_REF-m);
        }
        return Pair.create(iList, mList);
    }

    private static void styleChart(XYChart chart) {
        XYStyler styler = chart.getStyler();
        styler.setYAxisMin(0d);
        styler.setYAxisMax(10d);
        styler.setPlotGridLinesVisible(true);
    }


}
