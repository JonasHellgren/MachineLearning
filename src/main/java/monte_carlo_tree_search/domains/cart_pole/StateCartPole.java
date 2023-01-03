package monte_carlo_tree_search.domains.cart_pole;

import common.RandUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

@ToString
@Getter
@EqualsAndHashCode
public class StateCartPole  implements StateInterface<CartPoleVariables> {

    CartPoleVariables variables;

    public StateCartPole(CartPoleVariables variables) {
        this.variables = variables;
    }

    public static StateCartPole newAllStatesAsZero() {
        return  new StateCartPole(CartPoleVariables.builder()
                .theta(0)
                .thetaDot(0)
                .x(0)
                .xDot(0)
                .build());
    }

    public static StateCartPole newRandom() {
        double thetaMax=EnvironmentCartPole.THETA_THRESHOLD_RADIANS;
        double thetaDotMax=EnvironmentCartPole.THETA_DOT_THRESHOLD_RADIANS;
        double xMax=EnvironmentCartPole.X_TRESHOLD;
        double xDotMax=EnvironmentCartPole.X_DOT_THRESHOLD;
        return  new StateCartPole(CartPoleVariables.builder()
                .theta(RandUtils.calcRandomFromInterval(-thetaMax,thetaMax))
                .thetaDot(RandUtils.calcRandomFromInterval(-thetaDotMax,thetaDotMax))
                .x(RandUtils.calcRandomFromInterval(-xMax,xMax))
                .xDot(RandUtils.calcRandomFromInterval(-xDotMax,xDotMax))
                .build());
    }

    public static StateCartPole newFromVariables(CartPoleVariables variables) {
        return new StateCartPole(variables);
    }

    public static StateCartPole newFromState(StateInterface<CartPoleVariables> stateCartPole) {
        return new StateCartPole(stateCartPole.getVariables());
    }

    @Override
    public StateInterface<CartPoleVariables> copy() {
        return newFromVariables(variables.copy());
    }

    @Override
    public void setFromReturn(StepReturnGeneric<CartPoleVariables> stepReturn) {
        variables=stepReturn.copyState().getVariables();
    }

}