package udemy_Java_AI_courses.AI4refined.qlearning_objoriented;

import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.StepReturn;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_sixrooms.SixRooms;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_sixrooms.SixRoomsAgentTabular;

import java.util.Random;

public class Run6RoomTabularAgent {

    public static void main(String[] args) {

		SixRooms env = new SixRooms();
		SixRoomsAgentTabular agent = new SixRoomsAgentTabular(env.parameters);
		final Random random = new Random();

		for (int iEpisode = 0; iEpisode < agent.NUM_OF_EPISODES; ++iEpisode) {
			int startState=env.parameters.discreteStateSpace.get(random.nextInt(env.parameters.discreteStateSpace.size()));
			agent.state.setVariable("roomNumber", startState);
			if (env.isTerminalState(agent.state)) continue;  // we do not want to start with the terminal state
			runEpisode(agent, env, (double) iEpisode/agent.NUM_OF_EPISODES);
		}

		env.PrintQsa(agent);
		env.PrintQsaBestAction(agent);
		env.showPolicy(agent);
	}

	private static void runEpisode(SixRoomsAgentTabular agent, SixRooms env, double fEpisodes) {
		// Q learning equation: Q[s][a] = Q[s][a] + alpha ( R[s][a] + gamma (max Q[sNew]) - Q[s][a] )
		// a single episode: the agent finds a path from state s to the exit state

		State sNew = new State();
		StepReturn stepReturn;
		do {
			int aChosen=agent.chooseAction(fEpisodes);
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
