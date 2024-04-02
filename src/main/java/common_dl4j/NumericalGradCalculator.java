package common_dl4j;

import com.codepoetics.protonpack.functions.TriFunction;
import lombok.AllArgsConstructor;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

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
public class NumericalGradCalculator {

    float eps;
    TriFunction<Pair<INDArray, INDArray>, IActivation, INDArray, Double> scoreFcn;


    public INDArray getGrad(INDArray labels, INDArray z, IActivation activationFn, INDArray mask) {
        INDArray zPlus = changePreOut(z,eps);
        INDArray zMin = changePreOut(z,-eps);
        INDArray lossPlus = Nd4j.scalar(scoreFcn.apply(Pair.create(labels, zPlus), activationFn, mask));
        INDArray lossMin = Nd4j.scalar(scoreFcn.apply(Pair.create(labels, zMin), activationFn, mask));
        return (lossPlus.sub(lossMin).div(2 * eps).castTo(DataType.FLOAT));
    }

    private INDArray changePreOut(INDArray z, float eps) {
        return z.dup().add(eps);
    }

    public INDArray getGradSoftMax(INDArray label, INDArray z, IActivation activationFn, INDArray mask) {

        if (label.rank() != 1 || z.rank() != 1) {
            throw new IllegalArgumentException("Label/z shall be rank one, one example only");
        }

        long nOut= label.length();
        INDArray dldz= Nd4j.zeros(nOut);
        for (long i = 0; i < nOut ; i++) {
            INDArray zPlus = getCloneWithChangedValueAtIndex(z, i, eps);
            INDArray zMin = getCloneWithChangedValueAtIndex(z, i, -eps);
            double lossPlusDouble=scoreFcn.apply(Pair.create(label,zPlus), activationFn, mask);
            double lossMinDouble=scoreFcn.apply(Pair.create(label,zMin), activationFn, mask);
            dldz.putScalar(i,(lossPlusDouble-lossMinDouble)/(2*eps));
        }
        return dldz;
    }

    private INDArray getCloneWithChangedValueAtIndex(INDArray preOutput, long i, double eps) {
        var z= preOutput.dup();
        z.putScalar(i,z.getDouble(i)+ eps);
        return z;
    }


}
