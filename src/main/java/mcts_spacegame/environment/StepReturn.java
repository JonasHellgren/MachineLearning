package mcts_spacegame.environment;

import lombok.Builder;
import lombok.ToString;
import mcts_spacegame.models_space.State;

@Builder
@ToString
public class StepReturn {
    public State newPosition;
    public boolean isTerminal;
    public boolean isFail;
    public double reward;

    public StepReturn copy() {
        return StepReturn.builder().
                newPosition(this.newPosition.copy())
                .isTerminal(isTerminal)
                .isFail(isFail)
                .reward(reward)
                .build();
    }

}
