package common.runners;

import common.linear_regression_batch_fitting.LinearBatchFitter;
import common.math.LinearFitter;
import lombok.extern.java.Log;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import org.knowm.xchart.*;
import org.knowm.xchart.style.XYStyler;

@Log
public class RunnerLinearBatchFitter {
    public static final int N_POINTS = 100;
    public static final double W0 = 1;
    public static final double W1 = 0;
    public static final double B = 0;
    public static final int N_FEAT = 2;
    public static final double NOISE = 0.1;
    public static final double LEARNING_RATE = 3e-2;
    public static final int N_EPOCHS = 100;
    static java.util.Random rand = new java.util.Random(42);


    public static void main(String[] args) {
        var dataSet = createData();
        var w0VecForSize1=getW0Evolution(dataSet,1);
        var w0VecForSize10=getW0Evolution(dataSet,10);
        plot(w0VecForSize1, w0VecForSize10);
    }

    private static void plot(double[] w0VecForSize1, double[] w0VecForSize5) {
        XYChart chart = new XYChartBuilder()
                .xAxisTitle("epoch").yAxisTitle("w0").width(600).height(400).build();
        XYStyler styler = chart.getStyler();
        styler.setPlotGridLinesVisible(true);
        styler.setMarkerSize(1);
        styler.setLegendVisible(true);
        chart.addSeries("size 1", null, w0VecForSize1);
        chart.addSeries("size 10", null, w0VecForSize5);
        new SwingWrapper<>(chart).displayChart();
    }

    static double[] getW0Evolution(Pair<RealMatrix, RealVector> dataSet, int batchSize) {
        RealVector wAndBias = new ArrayRealVector(N_FEAT + 1, 0.0);
        var fitter = new LinearBatchFitter(LEARNING_RATE);
        double[] w0Arr=new double[N_EPOCHS];
        for (int epoch = 0; epoch < N_EPOCHS; epoch++) {
            w0Arr[epoch]=wAndBias.getEntry(0);
            var batchData = LinearBatchFitter.createBatch(dataSet, batchSize);
            wAndBias = fitter.fit(wAndBias, batchData);
        }
        log.info("Final weights: " + LinearBatchFitter.weights(wAndBias));
        log.info("Final b: " + LinearBatchFitter.bias(wAndBias));
        return w0Arr;
    }


    static Pair<RealMatrix, RealVector> createData() {
        var xMat = new Array2DRowRealMatrix(N_POINTS, N_FEAT);
        var yVec = new ArrayRealVector(N_POINTS);
        var trueWeights = new ArrayRealVector(new double[]{W0, W1});
        for (int i = 0; i < N_POINTS; i++) {
            double[] features = {10 * (rand.nextDouble() - 0.5), 10 * (rand.nextDouble() - 0.5)};
            double target = trueWeights.dotProduct(new ArrayRealVector(features)) + B +
                    NOISE * (rand.nextGaussian() - 0.5);
            xMat.setRow(i, features);
            yVec.setEntry(i, target);
        }
        return Pair.create(xMat, yVec);
    }


}
