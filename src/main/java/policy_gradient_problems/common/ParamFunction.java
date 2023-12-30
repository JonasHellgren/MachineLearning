package policy_gradient_problems.common;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class ParamFunction {

    ArrayRealVector params;

    public ParamFunction(ArrayRealVector params) {
        this.params = params;
    }

    public ParamFunction(double[] thetaArray) {
        this.params=new ArrayRealVector(thetaArray);
    }

    public void change(RealVector change) {
       params=params.add(change);
    }

    public double[] toArray() {
        return params.toArray();
    }

    public double getEntry(int i) {
        return params.getEntry(i);
    }

}
