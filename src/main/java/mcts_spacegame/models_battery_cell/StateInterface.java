package mcts_spacegame.models_battery_cell;

/**
 * SSV is generic type for the set of state variables.
 */

public interface StateInterface<SSV> {

    SSV getVariables();
    StateInterface<SSV> copy();
    void setFromReturn(StepReturnGeneric<SSV> stepReturn);

}
