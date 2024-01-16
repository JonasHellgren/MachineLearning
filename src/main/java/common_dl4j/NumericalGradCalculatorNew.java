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
public class NumericalGradCalculatorNew {

    double eps;
    TriFunction<Pair<INDArray,INDArray>,IActivation,INDArray,INDArray> scoreFcn;

    public INDArray getGrad(INDArray yRef, INDArray z, IActivation activationFn, INDArray mask) {

        long nPoints= yRef.rows();
        long nofOut= yRef.rank();
        INDArray dldz= Nd4j.zeros(nPoints,nofOut);
        for (int i = 0; i < nPoints ; i++) {  //todo fimpa for loop
            INDArray zPlus = getCloneWithChangedValueAtIndex(z.getRow(i), i, eps);
            INDArray zMin = getCloneWithChangedValueAtIndex(z.getRow(i), i, -eps);
            INDArray lossPlus=scoreFcn.apply(Pair.create(yRef.getRow(i),zPlus), activationFn, mask);
            INDArray lossMin=scoreFcn.apply(Pair.create(yRef.getRow(i),zMin), activationFn, mask);
            INDArray lossAtPoint = lossPlus.sub(lossMin).div(2 * eps);
            dldz.getRow(i).addi(lossAtPoint);
        }
        return dldz;
    }

    @NotNull
    private INDArray getCloneWithChangedValueAtIndex(INDArray preOutput, long i, double eps) {
        var z= preOutput.dup();
        return z.add(eps);
    }


}
