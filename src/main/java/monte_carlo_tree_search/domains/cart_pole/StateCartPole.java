package monte_carlo_tree_search.domains.cart_pole;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.models_battery_cell.CellVariables;
import monte_carlo_tree_search.domains.models_battery_cell.StateCell;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

@ToString
@Getter
@EqualsAndHashCode
public class StateCartPole  implements StateInterface<CartPoleVariables> {

    CartPoleVariables variables;

    public StateCartPole(CartPoleVariables variables) {
        this.variables = variables;
    }

    public static StateCartPole newWithVariables(CartPoleVariables variables) {
        return new StateCartPole(variables);
    }

    @Override
    public StateInterface<CartPoleVariables> copy() {
        return newWithVariables(variables.copy());
    }

    @Override
    public void setFromReturn(StepReturnGeneric<CartPoleVariables> stepReturn) {
        variables=stepReturn.copyState().getVariables();
    }
}
