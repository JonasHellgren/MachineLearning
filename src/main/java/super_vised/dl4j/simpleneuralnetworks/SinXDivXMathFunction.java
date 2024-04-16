package super_vised.dl4j.simpleneuralnetworks;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

/**
 * Calculate function value of sine of x divided by x.
 */
public class SinXDivXMathFunction implements MathFunction {

    @Override
    public INDArray getFunctionValues(final INDArray x) {
        return Transforms.sin(x, true).divi(x);
    }

    @Override
    public String getName() {
        return "SinXDivX";
    }
}
