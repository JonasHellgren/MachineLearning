package udemycourse.nn2.SinglePerceptron;

public class ActivationFunction {

	public static int stepFunction(float activation) {
		
		if( activation >= 1)
			return 1;
		
		return 0;
	}
}
