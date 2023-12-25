package common_dl4j;

import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.transforms.custom.SoftMax;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.ops.transforms.Transforms;


/**
 *  negate below because we want to maximize, want to find the net weights that maximizes

    policyGradientLoss=-logProb*advVec
    gradient=-(yRef-softMax)
 */


public class CustomPolicyGradientLoss  implements ILossFunction {

    @Override
    public double computeScore(INDArray yRef, INDArray preOutput, IActivation activationFn, INDArray mask, boolean average) {
        INDArray policyGradientLoss = getPolicyGradientLoss(yRef, preOutput);
        return -policyGradientLoss.sumNumber().doubleValue();
    }

    @Override
    public INDArray computeScoreArray(INDArray yRef, INDArray preOutput, IActivation activationFn, INDArray mask) {
        return getPolicyGradientLoss(yRef,preOutput).neg();
    }

    @Override
    public INDArray computeGradient(INDArray yRef, INDArray preOutput, IActivation activationFn, INDArray mask) {
        INDArray y = getSoftMax(preOutput);
        return yRef.sub(y).neg();  //todo - really correct
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

    private static INDArray getPolicyGradientLoss(INDArray yRef, INDArray preOutput) {
        INDArray y = getSoftMax(preOutput);
        INDArray logProb= Transforms.log(y);
        return yRef.mul(logProb);
    }

    private static INDArray getSoftMax(INDArray preOutput) {  //todo avoid new SoftMax -> as class field
        return Nd4j.getExecutioner().exec(new SoftMax(preOutput))[0];
    }

}
