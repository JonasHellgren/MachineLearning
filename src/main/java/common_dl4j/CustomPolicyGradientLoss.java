package common_dl4j;

import com.codepoetics.protonpack.functions.TriFunction;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.ops.transforms.Transforms;


/**
 * negate below because we want to maximize, want to find the net weights that maximizes entropy-loss
 * entropy-loss expresses how similar two distributions are, we want estimate probabilities to be as similar
 * to "true" (labeled) probabilities
 * policyGradientLoss=-logProb*advVec
 *
 * See entropy.md for more intuition
 */


public class CustomPolicyGradientLoss implements ILossFunction {

    public static final double EPS = 1e-5;
    double eps;
    NumericalGradCalculator gradCalculator;

    public static CustomPolicyGradientLoss newDefault() {
        return new CustomPolicyGradientLoss(EPS);
    }

    public CustomPolicyGradientLoss(double eps) {
        this.eps = eps;
        TriFunction<Pair<INDArray, INDArray>, IActivation, INDArray, Double> scoreFcn =
                (p, a, m) -> computeScore(p.getFirst(), p.getSecond(), a, m, false);
        gradCalculator = new NumericalGradCalculator(eps, scoreFcn);
    }

    @Override
    public double computeScore(INDArray yRef,
                               INDArray preOutput,
                               IActivation activationFn,
                               INDArray mask,
                               boolean average) {
        INDArray policyGradientLoss = getPolicyGradientLoss(yRef, preOutput, activationFn);
        return -policyGradientLoss.sumNumber().doubleValue();
    }

    @Override
    public INDArray computeScoreArray(INDArray yRef, INDArray preOutput, IActivation activationFn, INDArray mask) {
        return getPolicyGradientLoss(yRef, preOutput, activationFn).neg();
    }

    @Override
    public INDArray computeGradient(INDArray yRef, INDArray preOutput, IActivation activationFn, INDArray mask) {
        INDArray grad = gradCalculator.getGrad(yRef, preOutput, activationFn, mask);
        return grad.reshape(1,grad.length());  // reshape it to a row matrix of size 1×n
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
    private static INDArray getPolicyGradientLoss(INDArray yRef, INDArray preOutput, IActivation activationFn) {
        INDArray y = activationFn.getActivation(preOutput, false);
        INDArray logProb = Transforms.log(y);
        return yRef.mul(logProb);
    }


}
