package monte_carlo_tree_search.domains.cart_pole;

import lombok.Builder;
import lombok.ToString;
import monte_carlo_tree_search.domains.models_battery_cell.CellVariables;

@Builder
@ToString
public class CartPoleVariables {

    public double theta;
    public double x;
    public double thetaDot;
    public double xDot;
    public int nofSteps;

    public CartPoleVariables copy() {
        return CartPoleVariables.builder()
                .theta(theta).x(x).thetaDot(thetaDot).xDot(xDot).nofSteps(nofSteps).build();
    }

}
