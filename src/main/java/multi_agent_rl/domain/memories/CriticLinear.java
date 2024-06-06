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

    RealVector wAndBias;
    LinearBatchFitter fitter;

    public CriticLinear(int nFeatures, double learningRate) {
        this.nFeatures = nFeatures;
        this.wAndBias=new ArrayRealVector(nFeatures + N_BIAS_TERMS, VALUE_DEFAULT);
        this.fitter=new LinearBatchFitter(learningRate);
    }

    //todo constructor with value supplier

    public void fit(Pair<RealMatrix, RealVector> dataSet) {
        int batchSize=dataSet.getSecond().getDimension();
        var batchData = LinearBatchFitter.createBatch(dataSet, batchSize);
        wAndBias = fitter.fit(wAndBias, batchData);
    }

    public double predict(RealMatrix x) {
        return fitter.predict(x,wAndBias);
    }

}
