package super_vised.dl4j.regressionnetworks.function;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface MathFunctionInterface {

    INDArray getFunctionValues(INDArray x);

    String getName();
}