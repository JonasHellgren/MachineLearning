package common_dl4j;

import com.codepoetics.protonpack.functions.TriFunction;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * https://en.wikipedia.org/wiki/Finite_difference
 */

@AllArgsConstructor
public class NumericalGradCalculator {

    double eps;
    TriFunction<Pair<INDArray,INDArray>,IActivation,INDArray,Double> scoreFcn;

    public INDArray getGrad(INDArray yRef, INDArray z, IActivation activationFn, INDArray mask) {

        long nOut= yRef.size(0);
        INDArray dldz= Nd4j.zeros(nOut);
        for (long i = 0; i < nOut ; i++) {
            INDArray zPlus = getCloneWithChangedValueAtIndex(z, i, eps);
            INDArray zMin = getCloneWithChangedValueAtIndex(z, i, -eps);
            double lossPlus=scoreFcn.apply(Pair.create(yRef,zPlus), activationFn, mask);
            double lossMin=scoreFcn.apply(Pair.create(yRef,zMin), activationFn, mask);
            dldz.putScalar(i,(lossPlus-lossMin)/(2*eps));
        }
        return dldz;
    }

    @NotNull
    private INDArray getCloneWithChangedValueAtIndex(INDArray preOutput, long i, double eps) {
        var z= preOutput.dup();
        z.putScalar(i,z.getDouble(i)+ eps);
        return z;
    }


}
