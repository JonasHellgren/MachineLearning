package mcts_spacegame.environment;

import lombok.AllArgsConstructor;
import lombok.ToString;
import mcts_spacegame.models.State;

@AllArgsConstructor
@ToString
public class StepReturn {
    public State newPosition;
    public boolean isTerminal;
    public double reward;
}
