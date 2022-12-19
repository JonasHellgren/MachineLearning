package mcts_spacegame.models_battery_cell;

/***
 * SSV is set of state variables. AV is type for action variable
 */

public interface EnvironmentGenericInterface<SSV, AV> {

    StepReturnGeneric<SSV> step(ActionInterface<AV> action, StateInterface<SSV> state);

}