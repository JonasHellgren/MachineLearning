package mcts_spacegame.models_battery_cell;

public interface EnvironmentGenericInterface<SV, TA extends ActionInterface> {

    StepReturnGeneric<SV> step(TA action, StateInterface<SV> state);

}