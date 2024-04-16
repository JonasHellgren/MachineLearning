package safe_rl.domain.abstract_classes;

import org.apache.commons.math3.linear.RealVector;

import java.util.List;

public interface StateI<V> {
    V getVariables();
    void setVariables(V variables);
    StateI<V> copy();
    double[]  continousFeatures();
    int nContinousFeatures();
    int[]  discretFeatures();
    int hashCode();
    boolean equals(Object o);

}
