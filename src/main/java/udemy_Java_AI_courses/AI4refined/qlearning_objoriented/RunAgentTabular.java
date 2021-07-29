package udemy_Java_AI_courses.AI4refined.qlearning_objoriented;

import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms.SixRooms;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms.SixRoomsAgentTabular;

import java.util.Random;

public class RunAgentTabular {

    public static void main(String[] args) {

		SixRooms env = new SixRooms();
		SixRoomsAgentTabular agent = new SixRoomsAgentTabular(env.parameters);
		final Random random = new Random();

		for (int iEpisode = 0; iEpisode < agent.NUM_OF_EPISODES; ++iEpisode) {
			int startState=env.parameters.discreteStateSpace.get(random.nextInt(env.parameters.discreteStateSpace.size()));
			agent.state.setVariable("roomNumber", startState);
			if (env.isTerminalState(agent.state)) continue;  // we do not want to start with the terminal state
			runEpisode(agent, env);
		}

		agent.PrintQsa();
		agent.PrintQsaBestAction();
		agent.showPolicy(env);
	}

	private static void runEpisode(SixRoomsAgentTabular agent, SixRooms env) {
		// Q learning equation: Q[s][a] = Q[s][a] + alpha ( R[s][a] + gamma (max Q[sNew]) - Q[s][a] )
		// a single episode: the agent finds a path from state s to the exit state

		State sNew = new State();
		SixRooms.StepReturn stepReturn;
		do {
			int aChosen = (Math.random() < agent.PROBABILITY_RANDOM_ACTION) ?
					agent.chooseRandomAction(env.parameters.discreteActionsSpace) :
					agent.chooseBestAction(agent.state);

			stepReturn = env.step(aChosen, agent.state);
			sNew.copyState(stepReturn.state);
			double maxQ = agent.findMaxQ(sNew);
			double qOld = agent.readMemory(agent.state, aChosen);
			double qNew = qOld + agent.ALPHA * (stepReturn.reward + agent.GAMMA * maxQ - qOld);
			agent.writeMemory(agent.state, aChosen, stepReturn.termState ? stepReturn.reward : qNew);

			agent.state.copyState(sNew);
		} while (!stepReturn.termState);
	}

}
