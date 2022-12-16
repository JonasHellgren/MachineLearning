package mcts_spacegame.model_mcts;

import mcts_spacegame.models_battery_cell.StepReturnGeneric;

public interface StateInterface<TS extends StateInterface> {

    StateInterface<TS> copy();
    void setFromReturn(StepReturnGeneric<TS> stepReturn);

}
