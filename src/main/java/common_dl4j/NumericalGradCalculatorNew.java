package common_dl4j;

import com.codepoetics.protonpack.functions.TriFunction;
import lombok.AllArgsConstructor;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 *
 * Calculates gradient numerically, based on math described in
 * https://en.wikipedia.org/wiki/Finite_difference
 *
 * It is a major advantage to avoid deriving gradient analytically
 *
 * Imagine there is nPoints number of data points, each with nOut number of outputs
 * Hence labels will be a matrix with nPoints rows, each with rank nOut
 * lossPlus and lossMin will be matrices with nudged outputs for every item in a matrix with size as labels
 * lossPlus is the consequence of a small increase in the pre output layer, lossMin is from small decrease
 * The gradient has same size as labels. It is based on the difference between lossPlus and lossMin,
 * the finite difference.
 */

@AllArgsConstructor
public class NumericalGradCalculatorNew {

    double eps;
    TriFunction<Pair<INDArray, INDArray>, IActivation, INDArray, INDArray> scoreFcn;

    public INDArray getGrad(INDArray labels, INDArray z, IActivation activationFn, INDArray mask) {
        INDArray zPlus = changePreOut(z,eps);
        INDArray zMin = changePreOut(z,-eps);
        INDArray lossPlus = scoreFcn.apply(Pair.create(labels, zPlus), activationFn, mask);
        INDArray lossMin = scoreFcn.apply(Pair.create(labels, zMin), activationFn, mask);
        return lossPlus.sub(lossMin).div(2 * eps);
    }

    private INDArray changePreOut(INDArray z, double eps) {
        return z.dup().add(eps);
    }

}