package common_dl4j;

import com.codepoetics.protonpack.functions.TriFunction;
import lombok.SneakyThrows;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.ArrayList;
import java.util.List;


/**
 * Entropy-loss expresses how similar two distributions are, we want estimate probabilities (P) to be as similar
 * to "true" (oneHotGt) probabilities
 * Entropy-loss=-oneHotGt*log(P)
 * Negate below because we want to maximize, want to find the net weights that maximizes entropy-loss
 * <p>
 * See entropy.md for more intuition
 */


public class CustomPolicyGradientLoss implements ILossFunction {

    public static final double EPS = 1e-5, BETA = 0.1d;
    double eps, beta;
    NumericalGradCalculator gradCalculator;

    public static CustomPolicyGradientLoss newDefault() {
        return new CustomPolicyGradientLoss(EPS, BETA);
    }

    public static CustomPolicyGradientLoss newWithBeta(double beta) {
        return new CustomPolicyGradientLoss(EPS, beta);
    }

    public CustomPolicyGradientLoss(double eps, double beta) {
        this.eps = eps;
        this.beta = beta;
        TriFunction<Pair<INDArray, INDArray>, IActivation, INDArray, Double> scoreFcn =
                (p, a, m) -> computeScore(p.getFirst(), p.getSecond(), a, m, false);
        gradCalculator = new NumericalGradCalculator(eps, scoreFcn);
    }

    @SneakyThrows
    @Override
    public INDArray computeScoreArray(INDArray labels, INDArray preOutput, IActivation activationFn, INDArray mask) {
        throw  new NoSuchMethodException();
    }

    @Override
    public double computeScore(INDArray yRef,
                               INDArray preOutput,
                               IActivation activationFn,
                               INDArray mask,
                               boolean average) {

        INDArray estProbabilities = activationFn.getActivation(preOutput, false);
        double ce = EntropyCalculator.calcCrossEntropy(yRef, estProbabilities);
        double entropy = EntropyCalculator.calcEntropy(estProbabilities);
        double K = 1; //getK(estProbabilities);
        return ce - beta * K * entropy;
    }

    private static double getK(INDArray estProbabilities) {  //more aggressive entropy penalty
        double probMin1 = estProbabilities.minNumber().doubleValue();
        INDArray oneSubN = estProbabilities.rsub(1);
        double probMin2 = oneSubN.minNumber().doubleValue();
        double pMin = Math.min(probMin1, probMin2);
        return Math.min(100, Math.abs(Math.log(pMin / (1 - pMin))));
    }

    @Override
    public INDArray computeGradient(INDArray yRef, INDArray preOutput, IActivation activationFn, INDArray mask) {
        INDArray grad = gradCalculator.getGrad(yRef, preOutput, activationFn, mask);
        return grad.reshape(1, grad.length());  // reshape it to a row matrix of size 1×n
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

    //minimize cross-entropy, see entropy.md for details
    private static INDArray getPolicyGradientLoss(INDArray oneHotGt, INDArray preOutput, IActivation activationFn) {
        INDArray estProbabilities = activationFn.getActivation(preOutput, false);
        INDArray logP = Transforms.log(estProbabilities);
        return oneHotGt.mul(logP);
    }


}
