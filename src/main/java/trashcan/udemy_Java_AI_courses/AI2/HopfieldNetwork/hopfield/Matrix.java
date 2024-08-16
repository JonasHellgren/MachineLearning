package trashcan.udemy_Java_AI_courses.AI2.HopfieldNetwork.hopfield;

public class Matrix {

	public static double[] matrixVectorMultiplication(double[][] matrix, double[] vector) {
		
		double[] result = new double[vector.length];
		
		for(int i=0;i<matrix.length;++i) {
			double sum = 0;
			
			for(int j=0;j<matrix.length;++j)
				sum += matrix[i][j]  * vector[j];
			
			result[i] = sum;
		}
		
		return result;
	}
	
	public static double[][] createMatrix(int numOfRows, int numOfCols) {
		return new double[numOfRows][numOfCols];
	}
	
	public static double[][] outerProduct(double[] vector) {
		
		double[][] result = new double[vector.length][vector.length];
		
		for(int i=0;i<vector.length;++i)
			for(int j=0;j<vector.length;++j)
				result[i][j] = vector[i] * vector[j];
		
		return result;
	}
	
	public static double[][] clearDiagonals(double[][] matrix) {
		
		for(int i=0;i<matrix.length;++i)
			matrix[i][i] = 0;
		
		return matrix;
	}
	
	public static double[][] addMatrix(double[][] matrix1, double[][] matrix2) {
		
		double[][] result = new double[matrix1.length][matrix1.length];
		
		for(int i=0;i<matrix1.length;++i)
			for(int j=0;j<matrix1.length;++j)
				result[i][j] = matrix1[i][j] + matrix2[i][j];
		
		return result;
	}
}
