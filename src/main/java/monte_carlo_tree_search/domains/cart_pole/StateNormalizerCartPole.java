package monte_carlo_tree_search.domains.cart_pole;

import common.ScalerLinear;
import monte_carlo_tree_search.network_training.CartPoleStateValueMemory;

public class StateNormalizerCartPole<SSV> {

    private static final int RANGE_MIN = -1;
    private static final int RANGE_MAX = 1;
    ScalerLinear xScaler;
    ScalerLinear xDotScaler;
    ScalerLinear thetaScaler;
    ScalerLinear thetaDotScaler;

    public StateNormalizerCartPole() {
        double thetaMax=EnvironmentCartPole.THETA_THRESHOLD_RADIANS;
        double thetaDotMax=EnvironmentCartPole.THETA_DOT_THRESHOLD_RADIANS;
        double xMax=EnvironmentCartPole.X_TRESHOLD;
        double xDotMax=EnvironmentCartPole.X_DOT_THRESHOLD;
        thetaScaler=new ScalerLinear(-thetaMax, thetaMax, RANGE_MIN, RANGE_MAX);
        thetaDotScaler=new ScalerLinear(-thetaDotMax, thetaDotMax,RANGE_MIN, RANGE_MAX);
        xScaler=new ScalerLinear(-xMax, xMax,RANGE_MIN, RANGE_MAX);
        xDotScaler=new ScalerLinear(-xDotMax, xDotMax,RANGE_MIN, RANGE_MAX);
    }

   public CartPoleVariables normalize(SSV variables) {

       CartPoleVariables cv=(CartPoleVariables) variables;
       return CartPoleVariables.builder()
               .x(xScaler.calcOutDouble(cv.x))
               .xDot(xDotScaler.calcOutDouble(cv.xDot))
               .theta(thetaScaler.calcOutDouble(cv.theta))
               .thetaDot(thetaDotScaler.calcOutDouble(cv.thetaDot))
               .build();

   }

}
