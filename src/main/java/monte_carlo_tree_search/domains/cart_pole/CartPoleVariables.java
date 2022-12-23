package monte_carlo_tree_search.domains.cart_pole;

import lombok.Builder;
import lombok.ToString;
import monte_carlo_tree_search.domains.models_battery_cell.CellVariables;

@Builder
@ToString
public class CartPoleVariables {

    private static final int NOF_STEPS_DEFAULT = 0;
    public double theta;
    public double x;
    public double thetaDot;
    public double xDot;
    @Builder.Default
    public int nofSteps= NOF_STEPS_DEFAULT;

    public CartPoleVariables copy() {
        return CartPoleVariables.builder()
                .theta(theta).x(x).thetaDot(thetaDot).xDot(xDot).nofSteps(nofSteps).build();
    }

}
