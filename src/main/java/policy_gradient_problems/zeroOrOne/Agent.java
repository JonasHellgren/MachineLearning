package policy_gradient_problems.zeroOrOne;

import common.RandUtils;
import lombok.Builder;
import lombok.Getter;

import static policy_gradient_problems.helpers.LambdaFunctions.sigmoid;

@Builder
@Getter
public class Agent {

    public static final double THETA = 0.5;
    @Builder.Default
    Double theta = THETA;

    public  static  Agent newDefault() {
        return Agent.builder().build();
    }

    public void setTheta(Double theta) {
        this.theta = theta;
    }

    public int chooseAction() {
        return RandUtils.getRandomDouble(0,1)<getProbOne()
                ? 1
                : 0;
    }

    public  Double getProbOne() {
        return sigmoid.apply(theta);
    }

    //see md file for derivation
    double gradLogPolicy(int action) {
        return (action==0)
                ? -sigmoid.apply(theta)
                : 1-sigmoid.apply(theta);
    }

}
