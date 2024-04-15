package common.math;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.commons.math3.util.Pair;

/***
 *    yPred=θ0*x0+θ1*x1..+θn*xn
      where xn is 1, because θn is bias/intercept term
      Using x0 as bias is more "traditional" but complicates code

 *    Parameter updating:     θi=θi+αLearn*e*xi
 */

public class LinearFitter {

    public static final double ALPHA = 1e-1;
    public static final String ARGUMENT_ERROR_MSG = "Bad dimension x, nDim x=";

    Double alphaLearning;
    Integer nDim;

    @Getter
    double[] theta;

    public static LinearFitter ofLearningRateAndNDim(double alphaLearning, Integer nDim) {
        return new LinearFitter(alphaLearning, nDim);
    }

    public static LinearFitter ofNDim(Integer nDim) {
        return new LinearFitter(ALPHA, nDim);
    }

    public LinearFitter(Double alphaLearning, Integer nDim) {
        this.alphaLearning = alphaLearning;
        this.nDim = nDim;
        this.theta = new double[nDim + 1];
    }

    public void fit(Pair<Double, double[]> point) {
        fit(point.getFirst(),point.getSecond());
    }

    public void fit(double y, double[] xArr) {
        Preconditions.checkArgument(xArr.length == nDim, ARGUMENT_ERROR_MSG + xArr.length);
        double yPred = predict(xArr);
        double e = y - yPred;
        fitFromError(xArr, e);

    }

    public void fitFromError(double[] xArr, double e) {
        for (int i = 0; i < nDim + 1; i++) {
            double x = i == nDim  ? 1 : xArr[i];
            theta[i]=theta[i] + alphaLearning * e * x;
        }
    }

    public double predict(double[] x) {
        Preconditions.checkArgument(x.length == nDim, ARGUMENT_ERROR_MSG + x.length);
        double yPred = theta[nDim] * 1;  //xn=1
        for (int i = 0; i < nDim; i++) {
            yPred += yPred + theta[i] * x[i];
        }
        return yPred;
    }

}
