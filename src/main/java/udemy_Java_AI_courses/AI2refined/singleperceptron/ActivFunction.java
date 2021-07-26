package udemycourse.nn2refined.singleperceptron;

public class ActivFunction {
    public static int stepFunction(float activation) {

        if( activation >= 1)
            return 1;

        return 0;
    }
}
