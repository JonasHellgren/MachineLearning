package monte_carlo_tree_search.interfaces;

import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;

/***
 * SSV is set of stateNew variables. AV is type for action variable
 */

public interface EnvironmentGenericInterface<S, A> {

    StepReturnGeneric<S> step(ActionInterface<A> action, StateInterface<S> state);

}