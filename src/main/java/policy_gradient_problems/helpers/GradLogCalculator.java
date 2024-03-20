package policy_gradient_problems.helpers;

import lombok.NonNull;

import java.util.List;

/***
 *
 Derivation of grad log for soft max is given in
 https://towardsdatascience.com/derivative-of-the-softmax-function-and-the-categorical-cross-entropy-loss-ffceefc081d1

 The essential relations are (1)-(3), equation 2 can be seen as abstract definition of gradLog of soft max.
 (1) smi=exp(thetai)/sum i..n (exp(thetai))     i corresponds to action index
 (2) d/dthetaj log(smi) = 1/smi*dsmi/dthetaj    j corresponds to theta index

 (3) => d/dthetaj log(smi) = dirac(i,j)-smj

 (4)  dsmi/dthetaj = smi*d/dthetaj log(smi)=smi*(dirac(i,j)-smj)   (change in the softmax inputs)

 dirac(i,j) = | 1   (i==j)
              | 0   (else)

 dirac also defined in
 https://en.wikipedia.org/wiki/Dirac_delta_function

 */

public class GradLogCalculator {

    public static double[] calculateGradLog(final int action, @NonNull final List<Double> actionProbabilities) {
        int nofActions = actionProbabilities.size();
        double[] gradLogArray = new double[nofActions];
        for (int j = 0; j < nofActions; j++) {
            double dirac = (j == action) ? 1d : 0d;
            double smj = actionProbabilities.get(j);
            gradLogArray[j] = dirac - smj;
        }
        return gradLogArray;
    }


}
