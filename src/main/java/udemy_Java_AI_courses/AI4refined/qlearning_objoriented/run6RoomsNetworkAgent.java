package udemy_Java_AI_courses.AI4refined.qlearning_objoriented;

import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.Experience;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.StepReturn;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_sixrooms.SixRooms;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_sixrooms.SixRoomsAgentNeuralNetwork;

import java.util.List;
import java.util.Random;

public class run6RoomsNetworkAgent {
    public static void main(String[] args) {

        SixRooms env = new SixRooms();
        SixRoomsAgentNeuralNetwork agent = new SixRoomsAgentNeuralNetwork(env.parameters);
        final Random random = new Random();
        final int NOF_EPISODES_BETWEEN_PRINTOUTS =100;

        for (int iEpisode = 0; iEpisode < agent.NUM_OF_EPISODES; ++iEpisode) {
            if (iEpisode  % NOF_EPISODES_BETWEEN_PRINTOUTS == 0 & iEpisode>0)
                System.out.println("iEpisode:"+iEpisode+" bellmanError:"+agent.getBellmanErrorAverage(NOF_EPISODES_BETWEEN_PRINTOUTS));
            int startState = random.nextInt(env.parameters.nofStates);
            agent.state.setVariable("roomNumber", startState);
            if (env.isTerminalState(agent.state)) continue;  // we do not want to start with the terminal state

            runEpisode(agent,env,(double) iEpisode/agent.NUM_OF_EPISODES);    }

        env.PrintQsa(agent);
        env.PrintQsaBestAction(agent);
        env.showPolicy(agent);
    }

    private static void runEpisode(SixRoomsAgentNeuralNetwork agent,SixRooms env, double fEpisodes) {
        // Q learning equation: Q[s][a] = Q[s][a] + alpha ( R[s][a] + gamma (max Q[sNew]) - Q[s][a] )
        // a single episode: the agent finds a path from state s to the exit state

        StepReturn stepReturn;
        State sNew = new State();
        int miniBatchSize=SixRoomsAgentNeuralNetwork.MINI_BATCH_SIZE;
        int nofSteps=0;
        final int NOF_STEPS_MAX =10;

        do {
            int aChosen=agent.chooseAction(fEpisodes);
            stepReturn = env.step(aChosen, agent.state);
            Experience experience = new Experience(new State(agent.state), aChosen, stepReturn);
            agent.replayBuffer.addExperience(experience, agent.REPLAY_BUFFER_SIZE);

            List<Experience> miniBatch=agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(miniBatchSize,agent,fEpisodes);

            if (miniBatch.size()==miniBatchSize) {
                DataSetIterator iterator = agent.createTrainingData(miniBatch);
                agent.network.fit(iterator);
            }

            sNew.copyState(stepReturn.state);
            agent.state.copyState(sNew);

            nofSteps++;
        } while (!stepReturn.termState & nofSteps<NOF_STEPS_MAX);
    }

}
