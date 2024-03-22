package policy_gradient_problems.helpers;

import lombok.Getter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 * Critic memory in some agents
 */

@Getter
public class CriticMemoryParamDotProduct {

    ArrayRealVector wVector;   //value function parameters
    double alpha;

    public CriticMemoryParamDotProduct(int nofFeatures, double alpha) {
        this.wVector=new ArrayRealVector(nofFeatures);
        this.alpha=alpha;
    }

    public double getValue(ArrayRealVector state) {
        return wVector.dotProduct(state);
    }

    public  void update(ArrayRealVector state, double valueRef) {
        double delta=valueRef-getValue(state);
        RealVector deltaVector = state.copy().mapMultiplyToSelf(alpha * delta);
        wVector=wVector.add(deltaVector);  //w+alpha*delta*state
    }

}
