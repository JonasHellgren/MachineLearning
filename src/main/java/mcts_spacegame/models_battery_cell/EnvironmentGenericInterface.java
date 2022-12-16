package mcts_spacegame.models_battery_cell;

import mcts_spacegame.model_mcts.ActionInterface;
import mcts_spacegame.model_mcts.StateInterface;

public interface EnvironmentGenericInterface<TS extends StateInterface, TA extends ActionInterface> {

    StepReturnGeneric<TS> step(TA action, TS state);

}