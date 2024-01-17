package common_dl4j;

import com.codepoetics.protonpack.functions.TriFunction;
import common.ListUtils;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.ArrayList;
import java.util.List;

public class CustomPolicyGradientLossNew implements ILossFunction {

    public static final float EPS = 1e-3f;
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
    Remains the same for all loss functions
    Compute Score computes the average loss function across many datapoints.
    The loss for a single datapoint is summed over all output features.
     */

    @Override
    public double computeScore(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask, boolean average) {

        int nofExamples = labels.rows();
        int rank = labels.rank();
        System.out.println("nofExamples = " + nofExamples);
        System.out.println("rank = " + rank);
        INDArray scoreArrAllPoints=Nd4j.create(nofExamples,rank);
        System.out.println("scoreArrAllPoints.shapeInfoToString() = " + scoreArrAllPoints.shapeInfoToString());
        System.out.println("scoreArrAllPoints = " + scoreArrAllPoints);
        for (int i = 0; i < nofExamples; i++) {
            INDArray scoreArr = scoreOnePoint(labels, preOutput, activationFn, mask);

            System.out.println("scoreArr.shapeInfoToString() = " + scoreArr.shapeInfoToString());
            System.out.println("scoreArr = " + scoreArr);
            scoreArrAllPoints.getRow(i).addi(scoreArr);
        }
        double score = scoreArrAllPoints.sumNumber().doubleValue();
        score = getAverageScoreIfRequested(average, scoreArrAllPoints, score);
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

        System.out.println("\"computeScoreArray\" = " + "computeScoreArray");
        INDArray scoreArr = scoreOnePoint(labels, preOutput, activationFn, mask);
        return scoreArr.sum(1);
    }

    private INDArray scoreOnePoint(INDArray label,
                                   INDArray z,
                                   IActivation activationFn,
                                   INDArray mask) {


       // System.out.println("labels = " + labels);

        System.out.println("preOutput = " + z);

        List<Double> scoreList = new ArrayList<>();
        int nOut = label.rank();
        for (int i = 0; i < nOut; i++) {
            INDArray estProbabilities = activationFn.getActivation(z, false);
            double ce = EntropyCalculator.calcCrossEntropy(label, estProbabilities);
            double entropy = EntropyCalculator.calcEntropy(estProbabilities);
            double K = 1; //getK(estProbabilities);
            scoreList.add(ce - beta * K * entropy);
        }

       // System.out.println("scoreList = " + scoreList);

        //  scoreList.add(1d);
      //  scoreList.add(1d);

        //INDArray outArray = Nd4j.create(ListUtils.toArray(scoreList), new int[]{1, nOut});

        INDArray outArray =  Nd4j.create(new double[]{0,-1}, new int[]{2});
       // System.out.println("outArray = " + outArray);
        return outArray.castTo(DataType.FLOAT);

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

        INDArray grad1 = gradCalculator.getGradSoftMax(labels, preOutput, activationFn, null);
        System.out.println("grad = " + grad1);

        INDArray grad = Nd4j.create(new float[]{1,0}, new int[]{1, 2});
        return grad.castTo(DataType.FLOAT);
        /*        int nofExamples = labels.rows();
        long size = labels.rank();
        INDArray gradMat=Nd4j.create(nofExamples, size);
        for (int i = 0; i < nofExamples; i++) {
            INDArray yRef = labels.getRow(i);
            INDArray grad = gradCalculator.getGrad(yRef, preOutput, activationFn, mask);
            gradMat.add(grad);

        }
        return gradMat;  // reshape it to a row matrix of size 1×n*/
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
