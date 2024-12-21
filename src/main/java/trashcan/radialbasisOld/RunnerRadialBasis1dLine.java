package radialbasisOld;

import org.hellgren.utilities.list_arrays.Array2ListConverter;
import org.hellgren.utilities.list_arrays.ArrayCreator;
import org.hellgren.utilities.list_arrays.ListCreator;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.XYStyler;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RunnerRadialBasis1dLine {

    public static final double TOL = 1.0;
    public static final int N_FITS = 1000;
    public static final double MAX_X = 10d;
    public static final double MAX_Y = 10d;
    public static final int N_KERNELS = 6;
    public static final double SIGMA = 0.5*(MAX_X / (N_KERNELS - 1));
    public static final double LEARNING_RATE = 0.1;
    public static final int WIDTH = 400;
    public static final int HEIGHT = 200;
    public static final double NOISE = 2;
    static RadialBasis rbObo, rbBatch;
    static List<Double> in0;
    static List<List<Double>> inList;
    static List<Double> outList;

    public static void main(String[] args) {
        init();
        fitWeightsOneByOne(rbObo);
        showCorrelationChart(getOutRbfList(rbObo), "RBF-obo");
        showKernelChart(rbObo, "obo");
        fitWeightsBatch(rbBatch);
        showCorrelationChart(getOutRbfList(rbBatch), "RBF-batch");
        showKernelChart(rbBatch, "batch");
    }

    private static void init() {
        double[] centers = ArrayCreator.createArrayFromStartAndEndWithNofItems(0d-0*SIGMA, MAX_X+0*SIGMA, N_KERNELS);
        double[] sigmas = ArrayCreator.createArrayWithSameDoubleNumber(N_KERNELS, SIGMA);
        rbObo = RadialBasis.empty();
        rbObo.addKernelsWithCentersAndSigmas(centers, sigmas);
        rbBatch = RadialBasis.empty();
        rbBatch.addKernelsWithCentersAndSigmas(centers, sigmas);
        in0 = ListCreator.createFromStartToEndWithNofItems(0d, MAX_X, 10);
        inList = in0.stream().map(in -> List.of(in)).toList();
        outList = ListCreator.createFromStartToEndWithNofItems(0d, MAX_Y, 10);
    }


    private static void showCorrelationChart(List<Double> outRbfList, String name) {
        var chart = getChartCorrelation();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.addSeries("TAR", in0, outList);
        chart.addSeries(name, in0, outRbfList);
        new SwingWrapper<>(chart).displayChart();
    }


    private static void showKernelChart(RadialBasis rb1, String titleRbf) {
        var chartRbf = getChartRbf(titleRbf);
        List<Double> xData = getXData();
        List<Double> yData = Array2ListConverter.arrayToList(rb1.getWeights());
        chartRbf.addSeries(titleRbf, xData, yData);
        new SwingWrapper<>(chartRbf).displayChart();
    }


    static void fitWeightsBatch(RadialBasis rb1) {
        var updater = WeightUpdaterOld.withLearningRate(rb1, LEARNING_RATE);
        for (int i = 0; i < N_FITS; i++) {
            List<Double> outListNoisy = getListNoisy();
            updater.updateWeights(inList, outListNoisy);
        }
    }

    static void fitWeightsOneByOne(RadialBasis rb1) {
        var updater = WeightUpdaterOld.withLearningRate(rb1, LEARNING_RATE);
        for (int i = 0; i < N_FITS; i++) {
            List<Double> outListNoisy = getListNoisy();
            for (int j = 0; j < inList.size() ; j++) {
                updater.updateWeights(List.of(inList.get(j)),List.of(outListNoisy.get(j)));
            }
        }
    }


    private static List<Double> getXData() {
        List<Double> xData = new LinkedList<>();
        for (int i = 0; i < rbObo.nKernels(); i++) {
            xData.add(rbObo.getKernel(i).centerCoordinates()[0]);
        }
        return xData;
    }


    private static XYChart getChartRbf(String titleRbf) {
        XYChart chartRbf = new XYChartBuilder().title(titleRbf).width(WIDTH).height(HEIGHT).build();
        XYStyler styler = chartRbf.getStyler();
        styler.setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        styler.setChartTitleVisible(true);
        styler.setLegendVisible(false);
        styler.setChartBackgroundColor(Color.WHITE);
        styler.setMarkerSize(5);
        return chartRbf;
    }


    private static List<Double> getOutRbfList(RadialBasis rb1) {
        return  in0.stream().map(in -> rb1.outPut(List.of(in))).toList();
    }


    private static XYChart getChartCorrelation() {
        XYChart chart = new XYChartBuilder()
                .xAxisTitle("x").yAxisTitle("y").width(WIDTH).height(HEIGHT).build();
        XYStyler styler = chart.getStyler();
        styler.setYAxisMin(0d);
        styler.setYAxisMax(MAX_Y);
        styler.setChartBackgroundColor(Color.WHITE);
        styler.setPlotGridLinesVisible(true);
        styler.setXAxisDecimalPattern("#");
        return chart;
    }


    private static List<Double> getListNoisy() {
        var rand= new Random();
        return outList.stream()
                .map(value -> value + rand.nextGaussian() * NOISE)
                .toList();
    }


}
