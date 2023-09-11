package policy_gradient_upOrDown.domain;

import common.RandUtils;
import lombok.Builder;
import policy_gradient_upOrDown.helpers.LambdaFunctions;

@Builder
public class Agent {

    public static final double THETA = 0.5;
    @Builder.Default
    Double theta = THETA;

    public void setTheta(Double theta) {
        this.theta = theta;
    }

    public int chooseAction() {
        return RandUtils.getRandomDouble(0,1)<getProbOne()
                ? 1
                : 0;
    }

    public Double getProbOne() {
        return LambdaFunctions.sigmoid.apply(theta);
    }

}
