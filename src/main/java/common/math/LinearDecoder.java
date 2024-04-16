package common.math;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LinearDecoder {

    public static final String ARGUMENT_ERROR_MSG = "Bad dimension x, nDim x=";
    Integer nContFeatures;

    public double read(double[] x, double[] theta) {
        Preconditions.checkArgument(x.length == nContFeatures, ARGUMENT_ERROR_MSG + x.length);
        double yPred = theta[nContFeatures] * 1;  //xn=1
        for (int i = 0; i < nContFeatures; i++) {
            yPred += theta[i] * x[i];
        }
        return yPred;
    }

}
