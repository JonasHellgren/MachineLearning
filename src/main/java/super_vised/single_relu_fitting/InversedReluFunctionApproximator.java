package super_vised.single_relu_fitting;

import lombok.AllArgsConstructor;


/***
 * y =-a1*relu( -(x+a0) )
 *
 * Does not work as intented
 * dErrDa0/dErrDa1 maybe wrong
 */
@AllArgsConstructor
public class InversedReluFunctionApproximator {

    public static final double LEARNING_RATE = 0.01;
    public static final double A_0 = 0, A_1 = 0;
    double learningRate;
    double a0, a1;

    public static InversedReluFunctionApproximator defaultLearningRate() {
        return of(LEARNING_RATE);
    }

    public static InversedReluFunctionApproximator of(double learningRate) {
        return new InversedReluFunctionApproximator(learningRate, A_0, A_1);
    }


    public double y(double x) {
        return -a1 * Relu.relu(inputRelu(x));
    }


    public void fitParams(double yTar, double x) {
        double inputRelu = inputRelu(x);
        boolean isReluActivated = inputRelu > 0;
        //System.out.println("x="+x+" ,inputRelu = " + inputRelu+", isReluActivated = " + isReluActivated);

        double yEst = y(x);
        double err = yTar - yEst;

        double dErrDa0 = isReluActivated ? -a1 : 0;
        double a0New = isReluActivated ? a0 - learningRate * dErrDa0 * err : a0;

        double dErrDa1 = isReluActivated ? inputRelu : 0;
        double a1New = isReluActivated ? a1 - learningRate * dErrDa1 * err : a1;

        a0 = a0New;
        a1 = a1New;

    }


    private double inputRelu(double x) {
        return -(x + a0);
    }

    @Override
    public  String toString() {
        return "a0 = "+a0+", a1 = "+a1;
    }

}
