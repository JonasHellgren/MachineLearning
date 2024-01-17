package common_dl4j;

import com.codepoetics.protonpack.functions.TriFunction;
import common.ListUtils;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import java.util.ArrayList;
import java.util.List;

public class CustomPolicyGradientLossNew implements ILossFunction {

    static final float EPS = 1e-5f;
    static double BETA = 0.1d;
    double eps, beta;
    NumericalGradCalculatorNew gradCalculator;

    public static CustomPolicyGradientLossNew newDefault() {
        return new CustomPolicyGradientLossNew(EPS, BETA);
    }

    public static CustomPolicyGradientLoss newWithBeta(double beta) {
        return new CustomPolicyGradientLoss(EPS, beta);
    }

    public CustomPolicyGradientLossNew(double eps, double beta) {
        this.eps = eps;
        this.beta = beta;
        TriFunction<Pair<INDArray, INDArray>, IActivation, INDArray, INDArray> scoreFcn =
                (p, a, m) -> scoreOnePoint(p.getFirst(), p.getSecond(), a, m);
        this.gradCalculator = new NumericalGradCalculatorNew(EPS, scoreFcn);
    }

     /*
    computeScore computes the average loss function across many datapoints.
    The loss for a single datapoint is summed over all output features.
     */

    @Override
    public double computeScore(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask, boolean average) {
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
    public INDArray computeScoreArray(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask) {
        int nofPoints = labels.rows();
        int nofOut = labels.rank();
        INDArray scoreArrAllPoints = Nd4j.create(nofPoints, nofOut);
        for (int i = 0; i < nofPoints; i++) {
            INDArray scoreArr = scoreOnePoint(labels.getRow(i), preOutput.getRow(i), activationFn, mask);
            scoreArrAllPoints.getRow(i).addi(scoreArr);
        }
        return scoreArrAllPoints; 
    }

    private INDArray scoreOnePoint(INDArray label, INDArray z, IActivation activationFn, INDArray mask) {
        List<Double> scoreList = new ArrayList<>();
        int nofOut = label.rank();
        for (int i = 0; i < nofOut; i++) {
            INDArray estProbabilities = activationFn.getActivation(z, false);
            double ce = EntropyCalculator.calcCrossEntropy(label, estProbabilities);
            double entropy = EntropyCalculator.calcEntropy(estProbabilities);
            double K = 1; //getK(estProbabilities);
            scoreList.add(ce - beta * K * entropy);
        }

        INDArray outArray1 = Nd4j.create(ListUtils.toArray(scoreList), new int[]{nofOut});
        return outArray1.castTo(DataType.FLOAT);

    }

    private static double getK(INDArray estProbabilities) {  //more aggressive entropy penalty
        double probMin1 = estProbabilities.minNumber().doubleValue();
        INDArray oneSubN = estProbabilities.rsub(1);
        double probMin2 = oneSubN.minNumber().doubleValue();
        double pMin = Math.min(probMin1, probMin2);
        return Math.min(100, Math.abs(Math.log(pMin / (1 - pMin))));
    }


    private static double getAverageScoreIfRequested(boolean average, INDArray scoreArr, double score) {
        if (average) {
            score /= scoreArr.size(0);
        }
        return score;
    }

    @Override
    public INDArray computeGradient(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask) {
        int nofOut = labels.rank();
        int nofPoints = labels.rows();
        INDArray gradAllPoints = Nd4j.create(nofPoints, nofOut);
        for (int i = 0; i < nofPoints; i++) {
            INDArray grad1 = gradCalculator.getGradSoftMax(labels.getRow(i), preOutput.getRow(i), activationFn, null);
            gradAllPoints.getRow(i).addi(grad1);
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
        return "PolicyGradientLoss";
    }


}
