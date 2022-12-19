package mcts_spacegame.models_space;

import lombok.AllArgsConstructor;
import lombok.ToString;
import mcts_spacegame.generic_interfaces.ActionInterface;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@ToString
public class ActionShip implements ActionInterface<ShipActionSet> {

    private ShipActionSet actionValue;


    public static ActionShip newStill() {
        return new ActionShip(ShipActionSet.still);
    }
    public static ActionShip newUp() {
        return new ActionShip(ShipActionSet.up);
    }
    public static ActionShip newDown() {
        return new ActionShip(ShipActionSet.down);
    }

    public static ActionShip newNA() {
        return new ActionShip(ShipActionSet.nonApplicableAction());
    }

    @Override
    public void setValue(ShipActionSet actionValue) {
        this.actionValue=actionValue;
    }

    @Override
    public ShipActionSet getValue() {
        return actionValue;
    }

    @Override
    public Set<ShipActionSet> applicableActions() {
        return ShipActionSet.applicableActions();
    }

    @Override
    public ShipActionSet nonApplicableAction() {
        return ShipActionSet.nonApplicableAction();
    }
}
