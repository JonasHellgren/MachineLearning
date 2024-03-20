package policygradient.cart_pole;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.environments.cart_pole.ParametersPole;

 class TestParametersPole {


    ParametersPole parameters;
    @BeforeEach
     void init() {
        parameters=ParametersPole.newDefault();
    }

    @Test
     void givenDefault_thenCorrect() {
        System.out.println("parameters = " + parameters);
        Assertions.assertEquals(0.1,parameters.massPole());
    }

     @Test
     void givenWith_thenCorrect() {
         System.out.println("parameters = " + parameters);
         Assertions.assertEquals(1,parameters.withMassPole(1).massPole());
     }

}
