package testPolicyGradientUpOrDown;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import policy_gradient_zeroOrOne.helpers.ReturnCalculator;

import java.util.List;

public class TestHelpers {

    @Test
    public void givenReturnCalculator_thenCorrect() {
        ReturnCalculator calculator=new ReturnCalculator();
        List<Double> rewards=List.of(1d,2d,3d);
        List<Double> returns=calculator.calcReturns(rewards,1d);
        Assertions.assertEquals(List.of(6d,5d,3d),returns);
    }

    @Test
    public void givenReturnCalculatorGammaHalf_thenCorrect() {
        ReturnCalculator calculator=new ReturnCalculator();
        List<Double> rewards=List.of(1d,2d,3d);
        double gamma = 0.5;
        List<Double> returns=calculator.calcReturns(rewards, gamma);
        Assertions.assertEquals(List.of(1+2*gamma+3*gamma*gamma,2*gamma+3*gamma*gamma,3*gamma*gamma),returns);
    }

}
