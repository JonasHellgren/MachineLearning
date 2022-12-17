package mcts_spacegame.models_battery_cell;

import lombok.Builder;

@Builder
public class StepReturnGeneric<TS extends StateInterface> {
    public TS newState;
    public boolean isTerminal;
    public boolean isFail;
    public double reward;

    public StepReturnGeneric<TS> copy() {
        return  StepReturnGeneric.<TS>builder()
                .newState(copyState())
                .isTerminal(isTerminal)
                .isFail(isFail)
                .reward(reward)
                .build();
    }

    public TS copyState() {
        return (TS) this.newState.copy();
    }

}
