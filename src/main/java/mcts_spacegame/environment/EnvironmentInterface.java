package mcts_spacegame.environment;


import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.models_space.ShipVariables;
import mcts_spacegame.models_space.StateShip;

public interface EnvironmentInterface {

    StepReturnGeneric<ShipVariables> step(ShipAction action, StateInterface<ShipVariables> state);

}
