package policygradient.cart_pole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.the_problems.cart_pole.ParametersPole;

public class TestParametersPole {


    ParametersPole parameters;
    @BeforeEach
    public void init() {
        parameters=ParametersPole.newDefault();
    }

    @Test
    public void givenDefault_thenCorrect() {
        System.out.println("parameters = " + parameters);

    }


}
