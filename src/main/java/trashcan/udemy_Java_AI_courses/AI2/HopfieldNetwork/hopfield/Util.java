package trashcan.udemy_Java_AI_courses.AI2.HopfieldNetwork.hopfield;

public class Util {

	public static double[] transform(double[] pattern) {
		
		for(int i=0;i<pattern.length;++i)
			if( pattern[i] == 0 )
				pattern[i] = -1;
		
		return pattern;
	}
}
