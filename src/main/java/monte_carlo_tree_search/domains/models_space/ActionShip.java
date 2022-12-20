package monte_carlo_tree_search.domains.models_space;

import lombok.AllArgsConstructor;
import lombok.ToString;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;

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
        return new ActionShip(ShipActionSet.notApplicable);
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
    public ActionInterface<ShipActionSet> copy() {
        return new ActionShip(actionValue);
    }

    @Override
    public Set<ShipActionSet> applicableActions() {
        //return ShipActionSet.applicableActions();
        return new HashSet<>(Arrays.asList(ShipActionSet.up, ShipActionSet.still, ShipActionSet.down));
    }

    @Override
    public ShipActionSet nonApplicableAction() {
        //return ShipActionSet.nonApplicableAction();
        return ShipActionSet.notApplicable;
    }
}
