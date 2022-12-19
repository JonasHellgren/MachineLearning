package mcts_spacegame.models_battery_cell;

import lombok.Builder;

/**
 * This class holds the result of a step in the environment, SSV is the set of state variables.
 */

@Builder
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
