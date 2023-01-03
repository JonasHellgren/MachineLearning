package monte_carlo_tree_search.domains.cart_pole;

import common.ScalerLinear;

public class StateNormalizerCartPole {

    ScalerLinear xScaler;
    ScalerLinear xDotScaler;
    ScalerLinear thetaScaler;
    ScalerLinear thetaDotScaler;

    public StateNormalizerCartPole() {
        double thetaMax=EnvironmentCartPole.THETA_THRESHOLD_RADIANS;
        double thetaDotMax=EnvironmentCartPole.THETA_DOT_THRESHOLD_RADIANS;
        double xMax=EnvironmentCartPole.X_TRESHOLD;
        double xDotMax=EnvironmentCartPole.X_DOT_THRESHOLD;
        thetaScaler=new ScalerLinear(-thetaMax, thetaMax,0,1);
        thetaDotScaler=new ScalerLinear(-thetaDotMax, thetaDotMax,0,1);
        xScaler=new ScalerLinear(-xMax, xMax,0,1);
        xDotScaler=new ScalerLinear(-xDotMax, xDotMax,0,1);
    }

   public CartPoleVariables normalize(StateCartPole state) {

       return CartPoleVariables.builder()
               .x(xScaler.calcOutDouble(state.variables.x))
               .xDot(xDotScaler.calcOutDouble(state.variables.xDot))
               .theta(thetaScaler.calcOutDouble(state.variables.theta))
               .thetaDot(thetaDotScaler.calcOutDouble(state.variables.thetaDot))
               .build();

   }

}
