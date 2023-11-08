package policygradient.one_or_zero;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import policy_gradient_problems.helpers.LambdaFunctions;

public class TestLambdaFunctions {

    LambdaFunctions functions=new LambdaFunctions();

    @ParameterizedTest
    @CsvSource({"1000, 1",   "-1000, 0",})
    public  void givenSigmoid_whenApplySmall_thenIsZero(ArgumentsAccessor arguments) {
        double x=arguments.getDouble(0);
        double sig=arguments.getDouble(1);

        Assertions.assertEquals(sig, LambdaFunctions.sigmoid.apply(x));
    }

    //https://towardsdatascience.com/derivative-of-the-sigmoid-function-536880cf918e
    @ParameterizedTest
    @CsvSource({"0, 0.25",   "-1000, 0",        "1000, 0",})
    public  void givenDerSigmoid_whenApply_thenICorrect(ArgumentsAccessor arguments) {
        double x=arguments.getDouble(0);
        double dersig=arguments.getDouble(1);

        Assertions.assertEquals(dersig, functions.derSigmoid.apply(x));
    }


}
