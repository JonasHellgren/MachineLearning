package multi_agent_rl.domain.memories;

import common.linear_regression_batch_fitting.LinearBatchFitter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

public class CriticLinear {

    public static final int N_BIAS_TERMS = 1;
    public static final double VALUE_DEFAULT = 0.0;
    int nFeatures;

    RealVector weightsAndBias;
    LinearBatchFitter fitter;

    public CriticLinear(int nFeatures, double learningRate) {
        this(nFeatures, learningRate, VALUE_DEFAULT);
    }

    public CriticLinear(int nFeatures, double learningRate, double valueBias0) {
        this.nFeatures = nFeatures;
        this.weightsAndBias = new ArrayRealVector(nFeatures + N_BIAS_TERMS, VALUE_DEFAULT);
        this.weightsAndBias.setEntry(nFeatures,valueBias0);
        this.fitter = new LinearBatchFitter(learningRate);
    }

    public void fit(Pair<RealMatrix, RealVector> dataSet) {
        int batchSize = dataSet.getSecond().getDimension();
        var batchData = LinearBatchFitter.createBatch(dataSet, batchSize);
        weightsAndBias = fitter.fit(weightsAndBias, batchData);
    }

    public double predict(double[] x) {
        return fitter.predict(x, weightsAndBias);
    }
}
