package monte_carlo_tree_search.domains.cart_pole;

import common.MathUtils;
import lombok.Builder;
import lombok.ToString;
import monte_carlo_tree_search.domains.models_battery_cell.CellVariables;

@Builder
@ToString
public class CartPoleVariables {

    private static final int NOF_STEPS_DEFAULT = 0;
    private static final double DELTA = 0.01;
    public double theta;
    public double x;
    public double thetaDot;
    public double xDot;
    @Builder.Default
    public int nofSteps = NOF_STEPS_DEFAULT;

    public CartPoleVariables copy() {
        return CartPoleVariables.builder()
                .theta(theta).x(x).thetaDot(thetaDot).xDot(xDot).nofSteps(nofSteps).build();
    }

    @Override
    public boolean equals(Object obj) {

        //check if the argument is a reference to this object
        if (obj == this) return true;

        //check if the argument has the correct typ
        if (!(obj instanceof CartPoleVariables)) return false;

        //For each significant field in the class, check if that field matches the corresponding field of this object
        CartPoleVariables equalsSample = (CartPoleVariables) obj;
        boolean isSameTheta = MathUtils.compareDoubleScalars(equalsSample.theta,this.theta, DELTA);
        boolean isSameX = MathUtils.compareDoubleScalars(equalsSample.x,this.x, DELTA);
        boolean isSameThetaDot = MathUtils.compareDoubleScalars(equalsSample.thetaDot,this.thetaDot, DELTA);
        boolean isSameXDot= MathUtils.compareDoubleScalars(equalsSample.xDot,this.xDot, DELTA);
        return isSameTheta && isSameX && isSameThetaDot && isSameXDot;
    }

}
