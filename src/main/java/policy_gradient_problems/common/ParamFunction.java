package policy_gradient_problems.common;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class ParamFunction {

    RealVector params;

    public ParamFunction(RealVector params) {
        this.params = params;
    }

    public ParamFunction(double[] thetaArray) {
        this.params=new ArrayRealVector(thetaArray);
    }

    public void change(RealVector change) {
       params=params.add(change);
    }


    public ParamFunction copy() {
        return new ParamFunction(params.copy());
    }


    public double[] toArray() {
        return params.toArray();
    }

    public RealVector asRealVector() {
        return params;
    }

    public double getEntry(int i) {
        return params.getEntry(i);
    }

}
