package monte_carlo_tree_search.models_and_support_classes;

import lombok.Builder;
import lombok.ToString;
import monte_carlo_tree_search.interfaces.StateInterface;

/**
 * This class holds the result of a step in the environment, S is the set of stateNew variables.
 */

@Builder
@ToString
public class StepReturnGeneric<S> {
    public StateInterface<S> newState;
    public boolean isTerminal;
    public boolean isFail;
    public double reward;

    public StepReturnGeneric<S> copy() {
        return  StepReturnGeneric.<S>builder()
                .newState(copyState())
                .isTerminal(isTerminal)
                .isFail(isFail)
                .reward(reward)
                .build();
    }

    public StateInterface<S>  copyState() {
        return this.newState.copy();
    }

}
