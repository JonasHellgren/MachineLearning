package common.runners;

import lombok.extern.java.Log;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.util.Pair;
import java.util.Arrays;

@Log
public class MiniBatchGradientDescent {

    public static final double W0 = 1;
    public static final double W1 = 0;
    public static final double B = 0;
    public static final int N_POINTS = 100;
    public static final int N_FEAT = 2;
    public static final double NOISE = 0.1;
    static java.util.Random rand = new java.util.Random(42);

    public static void main(String[] args) {
        var dataSet=createData();
        int epochs = 1000;
        double learningRate = 1e-3;
        int batchSize = 3;
        RealVector w = new ArrayRealVector(N_FEAT, 0.0);
        double b = 0.0;
        log.info("Starting Mini-Batch Gradient Descent");
        for (int epoch = 0; epoch < epochs; epoch++) {
            var batchData=createBatch(dataSet,batchSize);
            var xMatBatch=batchData.getFirst();
            var yVecBatch=batchData.getSecond();
            var predictions = xMatBatch.operate(w).mapAdd(b);
            var errors = predictions.subtract(yVecBatch);
            var gradientsW = xMatBatch.transpose().operate(errors).mapMultiply(2.0/batchSize);
            double gradientBias = Arrays.stream(errors.toArray()).sum()*2/batchSize;
            w=w.subtract(gradientsW.mapMultiply(learningRate));
            b -= learningRate * gradientBias;
        }
        log.info("Final weights: " + w);
        log.info("Final b: " + b);
    }

    static  Pair<RealMatrix,RealVector> createData() {
        var xMat = new Array2DRowRealMatrix(N_POINTS, 2);
        var yVec = new ArrayRealVector(N_POINTS);
        var trueWeights = new ArrayRealVector(new double[]{W0, W1});
        for (int i = 0; i < N_POINTS; i++) {
            double[] features = {10 * (rand.nextDouble()-0.5), 10 * (rand.nextDouble()-0.5)};
            double target = trueWeights.dotProduct(new ArrayRealVector(features)) + B +
                    NOISE * (rand.nextGaussian()-0.5);
            xMat.setRow(i, features);
            yVec.setEntry(i, target);
        }
        return  Pair.create(xMat,yVec);
    }

    static  Pair<RealMatrix,RealVector> createBatch(Pair<RealMatrix,RealVector> dataSet, int batchSize) {
        var xMat=dataSet.getFirst();
        var yVec=dataSet.getSecond();
        int[] indices = rand.ints(0, N_POINTS).distinct().limit(batchSize).toArray();
        var xMatBatch = new Array2DRowRealMatrix(batchSize, 2);
        var yVecBatch = new ArrayRealVector(batchSize);
        int bi = 0;
        for (int j = 0; j < batchSize; j++) {
            xMatBatch.setRow(bi, xMat.getRow(indices[j]));
            yVecBatch.setEntry(bi, yVec.getEntry(indices[j]));
            bi++;
        }

        return  Pair.create(xMat,yVec);
    }


}
