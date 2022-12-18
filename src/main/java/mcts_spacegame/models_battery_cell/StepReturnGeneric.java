package mcts_spacegame.models_battery_cell;

import lombok.Builder;

@Builder
public class StepReturnGeneric<VARIABLES> {
    public StateInterface<VARIABLES> newState;
    public boolean isTerminal;
    public boolean isFail;
    public double reward;

    public StepReturnGeneric<VARIABLES> copy() {
        return  StepReturnGeneric.<VARIABLES>builder()
                .newState(copyState())
                .isTerminal(isTerminal)
                .isFail(isFail)
                .reward(reward)
                .build();
    }

    public StateInterface<VARIABLES>  copyState() {
        return this.newState.copy();
    }

}
