package common_dl4j;

import com.codepoetics.protonpack.functions.TriFunction;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.ops.transforms.Transforms;


/**
 * negate below because we want to maximize, want to find the net weights that maximizes
 * <p>
 * policyGradientLoss=-logProb*advVec
 * gradient=-(yRef-softMax)
 * <p>
 * Proof: yRef=Gt*yrVec; yrVec = one hot vector, one at action index, e.g. [1 0]
 * softMax = action probs, e.g. [0.5 0.5]
 * -(yrVec-softMax)=-[0.5 -0.5]=[-0.5 0.5]  <=> change net params so prob(a=0) increases, decreases for rest
 *
 */


public class CustomPolicyGradientLoss implements ILossFunction {

    public static final double EPS = 1e-5, EPS_DUMMY = 0d;
    double eps;
    boolean isNumGrad;
    NumericalGradCalculator gradCalculator;

    public static CustomPolicyGradientLoss newNotNum() {
        return new CustomPolicyGradientLoss(false, EPS_DUMMY);
    }

    public static CustomPolicyGradientLoss newNumDefault() {
        return new CustomPolicyGradientLoss(true, EPS);
    }

    public CustomPolicyGradientLoss(boolean isNumGrad, double eps) {
        this.eps = eps;
        this.isNumGrad = isNumGrad;
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
        return isNumGrad
                ? calcGradientNum(yRef, preOutput, activationFn, mask)
                : calcGradientNotNum(yRef, preOutput, activationFn);
    }

    private static INDArray calcGradientNotNum(INDArray yRef, INDArray preOutput, IActivation activationFn) {
        INDArray y = activationFn.getActivation(preOutput, false);
        return yRef.sub(y).neg();
    }

    public INDArray calcGradientNum(INDArray yRef, INDArray preOutput, IActivation activationFn, INDArray mask) {
        return gradCalculator.getGrad(yRef, preOutput, activationFn, mask);
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

    private static INDArray getPolicyGradientLoss(INDArray yRef, INDArray preOutput, IActivation activationFn) {
        INDArray y = activationFn.getActivation(preOutput, false);
        INDArray logProb = Transforms.log(y);
        return yRef.mul(logProb);
    }


}
