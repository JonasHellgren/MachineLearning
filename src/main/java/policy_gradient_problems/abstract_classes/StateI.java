package policy_gradient_problems.abstract_classes;

import java.util.List;

public interface StateI<V> {
    V getVariables();
    void setVariables(V variables);
    StateI<V> copy();
    List<Double> asList();
}
