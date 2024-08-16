package trashcan.udemy_Java_AI_courses.AI2.HopfieldNetwork.hopfield;

public class ActivationFunction {

	public static int stepFunction(double x) {
		
		if( x >= 0 )
			return 1;
		
		return -1;
	}
}
