package policy_gradient_problems.domain.param_memories;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 * Actor memory for some agent classes
 */

public class ActorMemoryParam {

    RealVector params;

    public ActorMemoryParam(RealVector params) {
        this.params = params;
    }

    public ActorMemoryParam(double[] thetaArray) {
        this.params=new ArrayRealVector(thetaArray);
    }

    public void change(RealVector change) {
       params=params.add(change);
    }


    public ActorMemoryParam copy() {
        return new ActorMemoryParam(params.copy());
    }


    public double[] toArray() {
        return params.toArray();
    }

    public RealVector asRealVector() {
        return params;
    }

    public double getValue(int i) {
        return params.getEntry(i);
    }

    public double getValue(int i,int offest) {
        return params.getEntry(i+offest);
    }


}
