package mcts_spacegame.environment;


import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.models_space.State;

public interface EnvironmentInterface {

    StepReturn step(ShipAction action, State state);

}
