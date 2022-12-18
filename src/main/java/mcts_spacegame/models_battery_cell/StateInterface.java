package mcts_spacegame.models_battery_cell;

public interface StateInterface<CV> {

    CV getVariables();
    StateInterface<CV> copy();
    void setFromReturn(StepReturnGeneric<CV> stepReturn);

}
