package monte_carlo_tree_search.interfaces;

import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;

/**
 * S is generic type for the set of stateNew variables.
 */

public interface StateInterface<S> {
    S getVariables();
    StateInterface<S> copy();
    void setFromReturn(StepReturnGeneric<S> stepReturn);
}
