package udemy_Java_AI_courses.AI4refined.qlearning;

public class Constants {

	private Constants() {
	}
	public static final int NUM_OF_STATES = 6;   // number of states
	public static final int NUM_OF_ACTIONS= 6;   // number of actions
	//rewards in states (used to initialize R[][])
	public static final double R_FAIL = -1e5;  //non allowed transition
	public static final double R_MOVE = -0.1;  //move transition
	public static final double R_EXIT = 100;  //finding exit state reward

	public static final int STATE_EXIT =5;

	public static final double GAMMA = 0.9;  // gamma discount factor
	public static final double ALPHA = 0.1;  // learning rate
	public static final int NUM_OF_EPISODES = 100000; // number of iterations
    
}
