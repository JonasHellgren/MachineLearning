package monte_carlo_tree_search.interfaces;

import java.util.Set;

public interface SimulationPolicyInterface<S, A> {
    ActionInterface<A> chooseAction(StateInterface<S> state);
    Set<A> availableActionValues(StateInterface<S> state);
}
