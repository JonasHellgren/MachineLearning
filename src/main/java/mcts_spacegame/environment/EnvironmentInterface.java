package mcts_spacegame.environment;


import mcts_spacegame.enums.Action;
import mcts_spacegame.models.State;

public interface EnvironmentInterface {

    StepReturn step(Action action, State state);

}
