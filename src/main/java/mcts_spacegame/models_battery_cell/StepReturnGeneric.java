package mcts_spacegame.models_battery_cell;

import lombok.Builder;

@Builder
public class StepReturnGeneric<CV> {
    public StateInterface<CV> newState;
    public boolean isTerminal;
    public boolean isFail;
    public double reward;

    public StepReturnGeneric<CV> copy() {
        return  StepReturnGeneric.<CV>builder()
                .newState(copyState())
                .isTerminal(isTerminal)
                .isFail(isFail)
                .reward(reward)
                .build();
    }

    public StateInterface<CV>  copyState() {
        return this.newState.copy();
    }

}
