package common.dl4j;

import com.codepoetics.protonpack.functions.TriFunction;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.ILossFunction;

/**
 *
 * Applies cross entropy method
 * The idea is to use the weighted cross entropy loss:
 * L=-sum(wi*[yi*log(pi))
 * Label argument in scoreOnePoint includes representation of wi and yi in equation above
 * The z argument in the same method is for pi
 * Big wi and differing probability distributions hence gives a big loss
 *
 */

public class LossCEM implements ILossFunction {

    static final float DEFAULT_EPS = 1e-5f;
    static final double DEFAULT_BETA = 0.1d;
    double eps;
    double beta;
    NumericalGradCalculator gradCalculator;

    public static LossCEM newDefault() {
        return new LossCEM(DEFAULT_EPS, DEFAULT_BETA);
    }

    public static LossCEM newWithBeta(double beta) {
        return new LossCEM(DEFAULT_EPS, beta);
    }

    public LossCEM(double eps, double beta) {
        this.eps = eps;
        this.beta = beta;
        TriFunction<Pair<INDArray, INDArray>, IActivation, INDArray, Double> scoreFcn =
                (p, a, m) -> scoreOnePoint(p.getFirst(), p.getSecond(), a);
        this.gradCalculator = new NumericalGradCalculator(DEFAULT_EPS, scoreFcn);
    }

     /*
    computeScore computes the average loss function across many datapoints.
    The loss for a single datapoint is summed over all output features.
     */

    @Override
    public double computeScore(INDArray labels,
                               INDArray preOutput,
                               IActivation activationFn,
                               INDArray mask,
                               boolean average) {
        INDArray scoreArrAllPoints = computeScoreArray(labels, preOutput, activationFn, mask);
        double score = scoreArrAllPoints.sumNumber().doubleValue();
        score = getAverageScoreIfRequested(average, scoreArrAllPoints, score);
        return score;
    }

    /*
  computeScoreArray computes the loss function for many datapoints.
  The loss for a single datapoint is the loss summed over all output features.
  Returns an array that is #of samples x size of the output feature
   */

    @Override
    public INDArray computeScoreArray(INDArray labels,
                                      INDArray preOutput,
                                      IActivation activationFn,
                                      INDArray mask) {
        int nofPoints = labels.rows();
        INDArray scoreArrAllPoints = getEmptyIndMatrix(labels);
        for (int i = 0; i < nofPoints; i++) {
            double score = scoreOnePoint(labels.getRow(i), preOutput.getRow(i), activationFn);
            scoreArrAllPoints.putScalar(i,score);
        }
        return scoreArrAllPoints;
    }

    /***
     * A critical method
     * label expresses the "target" action probabilites. One hot encoding for value of taken action.
     * labels [1,0,0] will give smaller action change compared to [10,0,0]
     */

    private double scoreOnePoint(INDArray label, INDArray z, IActivation activationFn) {
        INDArray estProbabilities = activationFn.getActivation(z, false);
        double ce = CrossEntropyCalculator.calcCrossEntropy(label, estProbabilities);
        double entropy = new EntropyCalculatorDiscreteActions().calcEntropy(estProbabilities);
        return ce - beta  * entropy;
    }

    /**
     *  Called when fitting net
     */

    @Override
    public INDArray computeGradient(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask) {
        int nofPoints = labels.rows();
        INDArray gradAllPoints = getEmptyIndMatrix(labels);
        for (int i = 0; i < nofPoints; i++) {
            INDArray grad = gradCalculator.getGradSoftMax(labels.getRow(i), preOutput.getRow(i), activationFn, null);
            Dl4JUtil.replaceRow(gradAllPoints, grad, i);
        }
        return gradAllPoints.castTo(DataType.FLOAT);
    }

    @Override
    public Pair<Double, INDArray> computeGradientAndScore(INDArray yRef,
                                                          INDArray preOutput,
                                                          IActivation activationFn,
                                                          INDArray mask,
                                                          boolean average) {

        return Pair.of(
                computeScore(yRef, preOutput, activationFn, mask, average),
                computeGradient(yRef, preOutput, activationFn, mask));
    }

    @Override
    public String name() {
        return toString();
    }


    @Override
    public String toString() {
        return "LossCEM";
    }

    private static INDArray getEmptyIndMatrix(INDArray labels) {
        return Nd4j.create(labels.rows(), labels.columns());
    }

    private static double getAverageScoreIfRequested(boolean average, INDArray scoreArr, double score) {
        if (average) {
            score /= scoreArr.size(0);
        }
        return score;
    }



}
