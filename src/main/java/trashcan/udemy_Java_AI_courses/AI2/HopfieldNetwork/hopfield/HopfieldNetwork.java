package trashcan.udemy_Java_AI_courses.AI2.HopfieldNetwork.hopfield;

public class HopfieldNetwork {

	private double[][] weightMatrix;

	public HopfieldNetwork(int dimension) {
		this.weightMatrix = new double[dimension][dimension];
	}

	public void train(double[] pattern) {
		
		double[] patternBipolar = Util.transform(pattern);
		
		double[][] patternMatrix = Matrix.createMatrix(patternBipolar.length, patternBipolar.length);
		
		patternMatrix = Matrix.outerProduct(patternBipolar);
		patternMatrix =Matrix.clearDiagonals(patternMatrix);
				
		this.weightMatrix = Matrix.addMatrix(this.weightMatrix, patternMatrix);
	}

	public void recall(double[] pattern) {

		double[] patternBipolar =Util.transform(pattern);

		double[] result = Matrix.matrixVectorMultiplication(this.weightMatrix, patternBipolar);

		for (int i = 0; i < patternBipolar.length; ++i) {
			result[i] = ActivationFunction.stepFunction(result[i]);
		}

		for (int i = 0; i < patternBipolar.length; ++i) {
			if (patternBipolar[i] != result[i]) {
				System.out.println("Pattern not recognized...");
				return;
			}
		}

		System.out.println("Pattern recognized...");
	}
}
