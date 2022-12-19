package mcts_spacegame.environment;

import lombok.Builder;
import lombok.ToString;
import mcts_spacegame.models_space.StateShip;

@Builder
@ToString
public class StepReturnREMOVE {
    public StateShip newPosition;
    public boolean isTerminal;
    public boolean isFail;
    public double reward;

    public StepReturnREMOVE copy() {
        return StepReturnREMOVE.builder()
                .newPosition(this.newPosition.copy())
                .isTerminal(isTerminal)
                .isFail(isFail)
                .reward(reward)
                .build();
    }

}
