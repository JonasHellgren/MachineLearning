package mcts_spacegame.models_battery_cell;

import lombok.Builder;
import mcts_spacegame.model_mcts.StateInterface;

@Builder
public class StepReturnGeneric<TS extends StateInterface> {
    public TS newState;
    public boolean isTerminal;
    public boolean isFail;
    public double reward;

    public StepReturnGeneric copy() {
        return  StepReturnGeneric.builder()
                .newState(this.newState.copy())
                .isTerminal(isTerminal)
                .isFail(isFail)
                .reward(reward)
                .build();
    }
}
