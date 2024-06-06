package common.linear_regression_batch_fitting;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * This class fits w and b in yi=xi*w+b, where xi and w are vectors, i is data point index
 * Argument: batchData includes a matrix and a vector, every row in matrix is feature values (x).
 * In vector a row is value (y)
 * applied update rule
 * w <- w-learningRate*gradW,    gradW=xMat'*errors*2/batchSize
 * b <- b-learningRate*gradB,    gradB=sum(errors)*2/batchSize
 * where errors=xMat*w-y
 */

@AllArgsConstructor
public class LinearBatchFitter {
    public static final double LEARNING_RATE = 1e-3;
    public static final int N_BIAS_FEAT = 1;
    static java.util.Random rand = new java.util.Random(42);

    double learningRate;

    public LinearBatchFitter() {
        this.learningRate = LEARNING_RATE;
    }

    /**
     * fit, batchData is assumed to include references for vector
     */

    public RealVector fit(RealVector weightsAndBias, Pair<RealMatrix, RealVector> batchData) {
        int nFeat = nFeatures(weightsAndBias);
        RealVector w = weights(weightsAndBias, nFeat);
        double b = bias(weightsAndBias, nFeat);
        var xMatBatch = batchData.getFirst();
        var yVecBatch = batchData.getSecond();
        int batchSize = yVecBatch.getDimension();
        var predictions = xMatBatch.operate(w).mapAdd(b);
        var errors = predictions.subtract(yVecBatch);
        return getWeightAndBias(nFeat, w, b, xMatBatch, batchSize, errors);
    }

    /**
     * fitFromErrors, batchData is assumed to include errors for vector
     */

    public RealVector fitFromErrors(RealVector weightsAndBias, Pair<RealMatrix, RealVector> batchData) {
        int nFeat = nFeatures(weightsAndBias);
        RealVector w = weights(weightsAndBias, nFeat);
        double b = bias(weightsAndBias, nFeat);
        var xMatBatch = batchData.getFirst();
        var errors = batchData.getSecond();
        int batchSize = errors.getDimension();
        return getWeightAndBias(nFeat, w, b, xMatBatch, batchSize, errors);
    }

    RealVector getWeightAndBias(int nFeat, RealVector w, double b, RealMatrix xMatBatch, int batchSize, RealVector errors) {
        var gradientsW = xMatBatch.transpose().operate(errors).mapMultiply(2.0 / batchSize);
        double gradientBias = Arrays.stream(errors.toArray()).sum() * 2 / batchSize;
        w = w.subtract(gradientsW.mapMultiply(learningRate));
        b -= learningRate * gradientBias;
        RealVector weightsAndBiasNew = new ArrayRealVector(nFeat + N_BIAS_FEAT, 0.0);
        weightsAndBiasNew.setSubVector(0, w);
        weightsAndBiasNew.setEntry(nFeat, b);
        return weightsAndBiasNew;
    }

    public double predict(RealMatrix x,RealVector weightsAndBias) {
        int nFeat=nFeatures(weightsAndBias);
        return x.operate(weights(weightsAndBias, nFeat)).mapAdd(bias(weightsAndBias, nFeat)).getEntry(0);
    }


    public static double bias(RealVector weightsAndBias) {
        return bias(weightsAndBias, nFeatures(weightsAndBias));
    }

    public static int nFeatures(RealVector weightsAndBias) {
        return weightsAndBias.getDimension() - N_BIAS_FEAT;
    }

    public static int nFeatures(RealMatrix xMat) {
        Preconditions.checkArgument(xMat.getRowDimension() > 0, "No rows in data matrix");
        return xMat.getRow(0).length;
    }

    public static RealVector weights(RealVector weightsAndBias) {
        return weights(weightsAndBias, nFeatures(weightsAndBias));
    }


    public static Pair<RealMatrix, RealVector> createBatch(Pair<RealMatrix, RealVector> dataSet, int batchSize) {
        Preconditions.checkArgument(batchSize > 0, "BatchSize must be larger than 0");
        int nofPoints=dataSet.getSecond().getDimension();
        Preconditions.checkArgument(batchSize <= nofPoints, "BatchSize must be smaller than n points");

        var xMat = dataSet.getFirst();
        var yVec = dataSet.getSecond();
        int[] indices = rand.ints(0, nofPoints).distinct().limit(batchSize).toArray();
        var xMatBatch = new Array2DRowRealMatrix(batchSize, nFeatures(xMat));
        var yVecBatch = new ArrayRealVector(batchSize);
        int bi = 0;
        for (int j = 0; j < batchSize; j++) {
            xMatBatch.setRow(bi, xMat.getRow(indices[j]));
            yVecBatch.setEntry(bi, bias(yVec, indices[j]));
            bi++;
        }
        return Pair.create(xMatBatch, yVecBatch);
    }


    private static double bias(RealVector weightsAndBias, int nFeat) {
        return weightsAndBias.getEntry(nFeat);
    }

    private static RealVector weights(RealVector weightsAndBias, int nFeat) {
        return weightsAndBias.getSubVector(0, nFeat);
    }


}
