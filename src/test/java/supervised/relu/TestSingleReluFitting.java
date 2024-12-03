package supervised.relu;

import org.junit.jupiter.api.Test;
import super_vised.single_relu_fitting.InversedReluFunctionApproximator;


/***
 * (y,x)=(-1,0.4),  (y,x)=(-2,0.3), (y,x)=(0,0.5)
 *
 * Does not work as intented
 *
 */

class TestSingleReluFitting {


    @Test
    void givenTBD_whenTBD_thenTBDt() {

        var function = new InversedReluFunctionApproximator(0.01, -0.5, 1.0);


        System.out.println("function.y(0.3) = " + function.y(0.3));
        System.out.println("function.y(0.6) = " + function.y(0.6));

        for (int i = 0; i < 1; i++) {
            function.fitParams(-1, 0.4);
            System.out.println("function = " + function);
            function.fitParams(-2, 0.3);
            System.out.println("function = " + function);
            function.fitParams(0.0, 0.5);
            System.out.println("function = " + function);
        }

        System.out.println("function = " + function);
        System.out.println("function.y(0.3) = " + function.y(0.3));
        System.out.println("function.y(0.6) = " + function.y(0.6));


    }

}
