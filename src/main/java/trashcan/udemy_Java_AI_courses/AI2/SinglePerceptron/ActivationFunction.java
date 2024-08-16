package trashcan.udemy_Java_AI_courses.AI2.SinglePerceptron;

public class ActivationFunction {

	public static int stepFunction(float activation) {
		
		if( activation >= 1)
			return 1;
		
		return 0;
	}
}
