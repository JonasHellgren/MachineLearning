package mcts_spacegame.generic_interfaces;

import mcts_spacegame.environment.StepReturnGeneric;

/***
 * SSV is set of state variables. AV is type for action variable
 */

public interface EnvironmentGenericInterface<SSV, AV> {

    StepReturnGeneric<SSV> step(ActionInterface<AV> action, StateInterface<SSV> state);

}