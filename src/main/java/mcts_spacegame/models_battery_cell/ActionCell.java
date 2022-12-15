package mcts_spacegame.models_battery_cell;

import lombok.Getter;
import mcts_spacegame.model_mcts.ActionInterface;

import java.util.List;

@Getter
public enum ActionCell implements ActionInterface {
    max(1f), moderate(0.8), low(0.5);

    private final double relativeCurrent;

    ActionCell(double relativeCurrent) {
        this.relativeCurrent = relativeCurrent;
    }



    @Override
    public List<ActionInterface> applicableActions() {
        return null;
    }

    @Override
    public List<ActionInterface> getAllActions(List<ActionInterface> actionsToSelected, ActionInterface actionOnSelected) {
        return null;
    }
}
