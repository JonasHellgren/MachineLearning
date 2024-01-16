package common_dl4j;

import com.codepoetics.protonpack.functions.TriFunction;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.ArrayList;
import java.util.List;

public class CustomPolicyGradientLossNew implements ILossFunction {

    public static final double EPS = 1e-5, BETA = 0.1d;
    double eps, beta;
    NumericalGradCalculator gradCalculator;

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
                (p, a, m) -> scoreArray(p.getFirst(), p.getSecond(), a, m);
      //  gradCalculator = new NumericalGradCalculator(eps, scoreFcn);
    }

    @Override
    public double computeScore(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask, boolean average) {
        INDArray scoreArr = scoreArray(labels, preOutput, activationFn, mask);
        double score = scoreArr.sumNumber().doubleValue();
        score = getAverageScoreIfRequested(average, scoreArr, score);
        return score;
    }

    /*
  Remains the same for all loss functions
  Compute Score computes the loss function for many datapoints.
  The loss for a single datapoint is the loss summed over all output features.
  Returns an array that is #of samples x size of the output feature
   */

    @Override
    public INDArray computeScoreArray(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask) {
        INDArray scoreArr = scoreArray(labels, preOutput, activationFn, mask);
        return scoreArr.sum(1);
    }

    private INDArray scoreArray(INDArray labels,
                                INDArray preOutput,
                                IActivation activationFn,
                                INDArray mask) {


        System.out.println("labels = " + labels);

        List<Double> scoreList = new ArrayList<>();
        int nofExamples = labels.rows();
        for (int i = 0; i < nofExamples; i++) {
            INDArray estProbabilities = activationFn.getActivation(preOutput, false);
            INDArray yRef = labels.getRow(i);
            System.out.println("yRef = " + yRef);

            double ce = EntropyCalculator.calcCrossEntropy(yRef, estProbabilities);
            double entropy = EntropyCalculator.calcEntropy(estProbabilities);
            double K = 1; //getK(estProbabilities);
            //scoreList.add(ce - beta * K * entropy);
        }

        scoreList.add(1d);

        INDArray outArray = Nd4j.create(scoreList);
        System.out.println("outArray = " + outArray);
        return outArray;

/*
        INDArray policyGradientLoss = getPolicyGradientLoss(yRef, preOutput, activationFn);
        double crossEntropy = -policyGradientLoss.sumNumber().doubleValue();
        return crossEntropy;
*/
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
        int nofExamples = labels.rows();
        long size = labels.rank();
        INDArray gradMat=Nd4j.create(nofExamples, size);
        for (int i = 0; i < nofExamples; i++) {
            INDArray yRef = labels.getRow(i);
            INDArray grad = gradCalculator.getGrad(yRef, preOutput, activationFn, mask);
            gradMat.add(grad);

        }
        return gradMat;  // reshape it to a row matrix of size 1×n
    }


/*
    @Override
    public INDArray computeGradient(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask) {
        INDArray grad = gradCalculator.getGrad(labels, preOutput, activationFn, mask);
        return grad.reshape(1, grad.length());  // reshape it to a row matrix of size 1×n
    }
*/

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

    //minimize cross-entropy, see entropy.md for details
    private static INDArray getPolicyGradientLoss(INDArray oneHotGt, INDArray preOutput, IActivation activationFn) {
        INDArray estProbabilities = activationFn.getActivation(preOutput, false);
        INDArray logP = Transforms.log(estProbabilities);
        return oneHotGt.mul(logP);
    }

        /*@Override
    public double computeScore(INDArray yRef,
                               INDArray preOutput,
                               IActivation activationFn,
                               INDArray mask,
                               boolean average) {

        System.out.println("yRef = " + yRef);
        INDArray estProbabilities = activationFn.getActivation(preOutput, false);
        double ce = EntropyCalculator.calcCrossEntropy(yRef, estProbabilities);
        double entropy=EntropyCalculator.calcEntropy(estProbabilities);
        double K = 1; //getK(estProbabilities);
        return ce-beta*K*entropy;
*/
/*
        INDArray policyGradientLoss = getPolicyGradientLoss(yRef, preOutput, activationFn);
        double crossEntropy = -policyGradientLoss.sumNumber().doubleValue();
        return crossEntropy;

} */
}
