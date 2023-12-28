package policy_gradient_problems.abstract_classes;

import java.util.List;

public interface StateInterface<S> {
    S getVariables();
    void set(StateInterface<S> state);
    StateInterface<S> copy();
    List<Double> asList();
}
