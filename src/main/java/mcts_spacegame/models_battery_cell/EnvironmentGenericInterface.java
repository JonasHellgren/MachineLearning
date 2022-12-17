package mcts_spacegame.models_battery_cell;

public interface EnvironmentGenericInterface<TS extends StateInterface, TA extends ActionInterface> {

    StepReturnGeneric<TS> step(TA action, TS state);

}