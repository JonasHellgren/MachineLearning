package radial_basis;

import common.list_arrays.List2ArrayConverter;
import common.other.CpuTimer;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;
import book_rl_explained.radialbasis.KernelProperties;
import book_rl_explained.radialbasis.RadialBasis;
import book_rl_explained.radialbasis.WeightUpdaterOld;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Log
public class RunnerRadialBasis1d {

    public static final int GAMMA = 100;
    public static final int N_FITS = 10_000;
    public static final int N_KERNELS = 10;
    static List<Double> IN_LIST1 = List.of(0.0, 0.2, 0.4, 0.6, 0.8, 1.0);
    static List<Double> OUT_LIST1 = List.of(0.0, 0.2, 0.4, 0.6, 0.8, 1.0);

    static List<Double> IN_LIST2 = List.of(0.0, 0.2, 0.4, 0.6, 0.8, 1.0);
    static List<Double> OUT_LIST2 = List.of(-10.0, -10d, -10d, 0.5, 0.5, 0.5);


    public static void main(String[] args) {
        var rb = getRadialBasis();
        List<Double> inList = IN_LIST2;
        List<Double> outList = OUT_LIST2;
        CpuTimer timer= new CpuTimer();
        fitRB(rb, inList, outList);
        log.info("time used fitting (ms) = " + timer.absoluteProgressInMillis());
        System.out.println(rb);
        plotOutRB(rb, inList, outList);
    }


    @NotNull
    private static RadialBasis getRadialBasis() {
        var kernels = new ArrayList<KernelProperties>();
        for (int i = 0; i <= N_KERNELS; i++) {
            double center = (double) i / N_KERNELS;
            kernels.add(KernelProperties.ofGammas(new double[]{center}, new double[]{GAMMA}));
        }
        return RadialBasis.ofKernels(kernels);
    }

    private static void fitRB(RadialBasis rb, List<Double> inList1, List<Double> outList) {
        List<List<Double>> inputs = inList1.stream()
                .map(Collections::singletonList).toList();
        var fitter = WeightUpdaterOld.of(rb);
        IntStream.range(0, N_FITS).forEach(i -> fitter.updateWeights(inputs, outList));
    }


    private static void plotOutRB(RadialBasis rb, List<Double> inList1, List<Double> outList) {
        var xData = List2ArrayConverter.convertListToDoubleArr(inList1);
        List<Double> yDataList = inList1.stream()
                .mapToDouble(in -> rb.outPut(List.of(in)))
                .boxed().toList();
        var yData = List2ArrayConverter.convertListToDoubleArr(yDataList);
        var chart = new XYChartBuilder().xAxisTitle("X").yAxisTitle("Y")
                .width(600).height(400)
                .build();
        chart.getStyler().setChartBackgroundColor(Color.WHITE); // set background color to blue
        addDataToChart(chart,
                List2ArrayConverter.convertListToDoubleArr(inList1),
                List2ArrayConverter.convertListToDoubleArr(outList), "Reference");
        addDataToChart(chart, xData, yData, "RBF");
        new SwingWrapper<>(chart).displayChart();
    }

    private static void addDataToChart(XYChart chart, double[] xData, double[] yData, String name) {
        XYSeries series = chart.addSeries(name, xData, yData);
        series.setMarker(SeriesMarkers.CIRCLE);
    }


}
