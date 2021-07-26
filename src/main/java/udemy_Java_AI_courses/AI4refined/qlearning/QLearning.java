package udemy_Java_AI_courses.AI4refined.qlearning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QLearning {

	private final double[][] R = {   //R(s,a) function
			{Constants.R_FAIL,Constants.R_FAIL,Constants.R_FAIL,Constants.R_FAIL,Constants.R_MOVE,Constants.R_FAIL},
			{Constants.R_FAIL,Constants.R_FAIL,Constants.R_FAIL,Constants.R_MOVE,Constants.R_FAIL,Constants.R_EXIT},
			{Constants.R_FAIL,Constants.R_FAIL,Constants.R_FAIL,Constants.R_MOVE,Constants.R_FAIL,Constants.R_FAIL},
			{Constants.R_FAIL,Constants.R_MOVE,Constants.R_MOVE,Constants.R_FAIL,Constants.R_MOVE,Constants.R_FAIL},
			{Constants.R_MOVE,Constants.R_FAIL,Constants.R_FAIL,Constants.R_MOVE,Constants.R_FAIL,Constants.R_EXIT},
			{Constants.R_FAIL,Constants.R_MOVE,Constants.R_FAIL,Constants.R_FAIL,Constants.R_MOVE,Constants.R_EXIT},
	};

    private final double[][] Q;  //Q(s,a) function
    private final Random random;
    
    public QLearning() {
    	this.random = new Random();
    	this.Q = new double[Constants.NUM_OF_STATES][Constants.NUM_OF_ACTIONS];
	}
    
    public void run() {
    	// episode: a full iteration when the agent starts from a random state and finds the terminal state
    	for(int iEpisode=0;iEpisode<Constants.NUM_OF_EPISODES;++iEpisode) {
    		int state = random.nextInt(Constants.NUM_OF_STATES);
    		if (isExitState(state)) continue;  // we do not want to start with the terminal state
    		simulate(state);
    	}
    }

	private void simulate(int state) {
		// Q learning equation: Q[s][a] = Q[s][a] + alpha ( R[s][a] + gamma (max Q[sNew]) - Q[s][a] )
		// a single episode: the agent finds a path from state s to the terminal state
		
		do {
			List<Integer> aSet = getFeasibleActions(state);  // get available actions
			int aChosen = chooseARandomAction(aSet);
			int sNew=doTransition(aChosen);
			double maxQ = findMaxQ(sNew);
			Q[state][aChosen] = Q[state][aChosen] +
					Constants.ALPHA*(R[state][aChosen]+Constants.GAMMA*maxQ-Q[state][aChosen]);
 			state = sNew;
		} while(!isExitState(state));
	}

	private int chooseARandomAction(List<Integer> aSet) {
		return aSet.get(random.nextInt(aSet.size()));
	}

	private boolean isExitState(int state) {
    	return (state==Constants.STATE_EXIT);
	}
	

	private double findMaxQ(int nextState) {
	// finding the max Q value for the next state
		double maxQ = Constants.R_FAIL;
		for(int i=0;i<this.Q.length;++i) {
			if( this.Q[nextState][i] > maxQ)
				maxQ = this.Q[nextState][i];
		}
		return maxQ;
	}

	private int doTransition(int aChoosen) {
    	return aChoosen;
	}
	
	private List<Integer> getFeasibleActions(int state) {
		
		List<Integer> possibleNextStates = new ArrayList<>();
		for(int action=0;action<Constants.NUM_OF_ACTIONS;++action) {
			if( this.R[state][action] > Constants.R_FAIL)  {
				possibleNextStates.add(action);
			}
		}
		
		return possibleNextStates;
	}

	public void printQmatrix() {
		
		for(int state=0;state<Constants.NUM_OF_STATES;++state) {
			for(int action=0;action<Constants.NUM_OF_ACTIONS;++action) {
				System.out.printf("%.1f ", this.Q[state][action]);
			}
			System.out.println();
		}
	}
	
	public void showPolicy() {
		// we consider every single state as a starting state
		// until we find the terminal state: we walk according to best action
		
		for(int startState=0; startState<Constants.NUM_OF_STATES; startState++) {

			int state=startState;
			System.out.print("Policy: " + state);
			while (!isExitState(state)) {
				int bestA = findBestAction(state);
				state=doTransition(bestA);
				System.out.print(" -> " + state);
			}
			System.out.println();
		}
	}

	private int findBestAction(int state) {

		int maxQState = 0;
		double maxQ = Constants.R_FAIL;
		for(int action=0;action<Constants.NUM_OF_ACTIONS;action++) {
			if( Q[state][action] > maxQ) {
				maxQ = Q[state][action];
				maxQState = action;
			}
		}
		return maxQState;
	}
}