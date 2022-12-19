package mcts_spacegame.environment;


import mcts_spacegame.enums.ShipActionREMOVE;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.models_space.ShipActionSet;
import mcts_spacegame.models_space.ShipVariables;

public interface EnvironmentInterface {

    StepReturnGeneric<ShipVariables> step(ActionInterface<ShipActionSet> action, StateInterface<ShipVariables> state);

}
