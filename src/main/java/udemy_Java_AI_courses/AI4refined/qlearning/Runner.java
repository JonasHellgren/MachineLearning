package udemy_Java_AI_courses.AI4refined.qlearning;

public class Runner {
 
    public static void main(String[] args) {
    	QLearning algorithm = new QLearning();
    	algorithm.run();
    	algorithm.printQmatrix();
    	algorithm.showPolicy();
	}
}
