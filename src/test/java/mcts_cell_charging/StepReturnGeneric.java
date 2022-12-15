package mcts_cell_charging;

import lombok.Builder;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.model_mcts.StateInterface;
import mcts_spacegame.models_space.State;

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
