package mcts_spacegame.models_battery_cell;

public interface EnvironmentGenericInterface<SSV, TA> {

    StepReturnGeneric<SSV> step(TA action, StateInterface<SSV> state);

}