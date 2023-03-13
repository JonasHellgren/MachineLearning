package monte_carlo_tree_search.interfaces;

import monte_carlo_tree_search.classes.StepReturnGeneric;

/**
 * SSV is generic type for the set of state variables.
 */

public interface StateInterface<SSV> {

    SSV getVariables();
    StateInterface<SSV> copy();
    void setFromReturn(StepReturnGeneric<SSV> stepReturn);

}
