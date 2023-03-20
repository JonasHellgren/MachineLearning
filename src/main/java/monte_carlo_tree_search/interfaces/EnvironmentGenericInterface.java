package monte_carlo_tree_search.interfaces;

import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;

/***
 * SSV is set of state variables. AV is type for action variable
 */

public interface EnvironmentGenericInterface<SSV, AV> {

    StepReturnGeneric<SSV> step(ActionInterface<AV> action, StateInterface<SSV> state);

}