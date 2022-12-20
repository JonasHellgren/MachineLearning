package monte_carlo_tree_search.classes;

import lombok.Builder;
import lombok.ToString;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

/**
 * This class holds the result of a step in the environment, SSV is the set of state variables.
 */

@Builder
@ToString
public class StepReturnGeneric<SSV> {
    public StateInterface<SSV> newState;
    public boolean isTerminal;
    public boolean isFail;
    public double reward;

    public StepReturnGeneric<SSV> copy() {
        return  StepReturnGeneric.<SSV>builder()
                .newState(copyState())
                .isTerminal(isTerminal)
                .isFail(isFail)
                .reward(reward)
                .build();
    }

    public StateInterface<SSV>  copyState() {
        return this.newState.copy();
    }

}
