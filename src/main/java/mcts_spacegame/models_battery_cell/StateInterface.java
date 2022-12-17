package mcts_spacegame.models_battery_cell;

public interface StateInterface<VARIABLES> {

    VARIABLES getVariables();
    StateInterface<VARIABLES> copy();
   // void setFromReturn(StepReturnGeneric<TS> stepReturn);

}
