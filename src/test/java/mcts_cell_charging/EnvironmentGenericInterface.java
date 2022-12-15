package mcts_cell_charging;

import mcts_spacegame.model_mcts.ActionInterface;
import mcts_spacegame.model_mcts.StateInterface;

public interface EnvironmentGenericInterface<TS extends StateInterface, TA extends ActionInterface> {

    StepReturnGeneric step(TA action, TS state);

}