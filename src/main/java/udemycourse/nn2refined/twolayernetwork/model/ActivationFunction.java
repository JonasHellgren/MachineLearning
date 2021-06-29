package udemycourse.nn2refined.twolayernetwork.model;

public class ActivationFunction {
    public static float sigmoid(float x) {
        return (float) (1 / (1 + Math.exp(-x)));
    }

    public static float dSigmoid(float x) {
        return x*(1-x); // because the output is the sigmoid(x) !!! we dont have to apply it twice
    }

    public static float dSigmoidAlt(float x) {
        return sigmoid(x)*(1-sigmoid(x)); //if using sum input
    }
}
