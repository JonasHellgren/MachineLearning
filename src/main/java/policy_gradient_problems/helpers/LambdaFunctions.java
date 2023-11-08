package policy_gradient_problems.helpers;

import java.util.function.Function;

public class LambdaFunctions {

    public static Function<Double,Double> sigmoid=(x) ->  1.0 / (1.0 + Math.exp(-x));

    public Function<Double,Double> derSigmoid = (x) ->
    {
        double sig = sigmoid.apply(x);
        return sig * (1 - sig);
    };


}
