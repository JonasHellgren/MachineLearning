package super_vised.dl4j.simpleneuralnetworks;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface MathFunction {

    INDArray getFunctionValues(INDArray x);

    String getName();
}