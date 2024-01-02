package policy_gradient_problems.abstract_classes;

import org.apache.commons.math3.linear.RealVector;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.List;

public interface StateI<V> {
    V getVariables();
    void setVariables(V variables);
    StateI<V> copy();
    List<Double> asList();
    RealVector asRealVector();
}
