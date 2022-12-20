package mcts_spacegame.generic_interfaces;

import mcts_spacegame.classes.StepReturnGeneric;

/**
 * SSV is generic type for the set of state variables.
 */

public interface StateInterface<SSV> {

    SSV getVariables();
    StateInterface<SSV> copy();
    void setFromReturn(StepReturnGeneric<SSV> stepReturn);

}
