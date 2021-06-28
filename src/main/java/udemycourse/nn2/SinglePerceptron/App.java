package udemycourse.nn2.SinglePerceptron;

public class App {

	public static void main(String[] args) {
		
		float[][] input = { {0,0}, {0,1}, {1,0}, {1,1} };
		float[] output = {0,1,1,1};  //OR
		
		Perceptron perceptron = new Perceptron(input, output);
		perceptron.train(0.1f);
		
		System.out.println("The error is 0 so our neural network is ready! Predictions: ");
		
		System.out.println(perceptron.calculateOutput(new float[]{0,0}));
		System.out.println(perceptron.calculateOutput(new float[]{0,1}));
		System.out.println(perceptron.calculateOutput(new float[]{1,0}));
		System.out.println(perceptron.calculateOutput(new float[]{1,1}));
		
	}
}
