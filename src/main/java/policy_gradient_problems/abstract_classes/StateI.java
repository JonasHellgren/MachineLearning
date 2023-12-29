package policy_gradient_problems.abstract_classes;

import java.util.List;

public interface StateI<S> {
    S getVariables();
    void set(StateI<S> state);
    StateI<S> copy();
    List<Double> asList();
}
