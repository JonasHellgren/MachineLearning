package qlearning_objoriented;

import org.junit.Test;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.Experience;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.StepReturn;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_sixrooms.SixRooms;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_sixrooms.SixRoomsAgentNeuralNetwork;

import java.util.List;
import java.util.Random;

public class TestLearningNeuralNetwork {

    State sNew = new State();
    SixRooms env = new SixRooms();
    SixRoomsAgentNeuralNetwork agent = new SixRoomsAgentNeuralNetwork(env.parameters);
    public final double SMALL = 0.001;
    private final Random random = new Random();

    @Test
    //https://www.saashanair.com/dqn-code/
    public void runLearningTextBook() {
        // episode: a full iteration when the agent starts from a random state and finds the terminal state
        env.PrintQsa(agent);
        for (int iEpisode = 0; iEpisode < agent.NUM_OF_EPISODES; ++iEpisode) {

            //System.out.println("iEpisode:"+iEpisode);
            int startState = random.nextInt(env.parameters.nofStates);
            agent.state.setVariable("roomNumber", startState);
            if (env.isTerminalState(agent.state)) continue;  // we do not want to start with the terminal state

            double probRandAction=agent.PROBABILITY_RANDOM_ACTION_START+
                    (agent.PROBABILITY_RANDOM_ACTION_END-agent.PROBABILITY_RANDOM_ACTION_START)*iEpisode/agent.NUM_OF_EPISODES;
            simulateTextBook(true,probRandAction);
        }

        env.PrintQsa(agent);
        System.out.println("nofFits:"+agent.nofFits);
        System.out.println(agent.network.summary());
        //System.out.println("replayBuffer"+agent.replayBuffer);
        env.showPolicy(agent);
    }

    private void simulateTextBook(boolean printFlag, double probRandAction) {
        // Q learning equation: Q[s][a] = Q[s][a] + alpha ( R[s][a] + gamma (max Q[sNew]) - Q[s][a] )
        // a single episode: the agent finds a path from state s to the exit state

        StepReturn stepReturn;
        int miniBatchSize=SixRoomsAgentNeuralNetwork.MINI_BATCH_MAXSIZE;
        int nofSteps=0;
        do {
            int aChosen = (Math.random() < probRandAction) ?
                    agent.chooseRandomAction(env.parameters.discreteActionsSpace) :
                    agent.chooseBestAction(agent.state);

            stepReturn = env.step(aChosen, agent.state);

            Experience experience = new Experience(new State(agent.state), aChosen, stepReturn);
            agent.replayBuffer.addExperience(experience, agent.REPLAY_BUFFER_MAXSIZE);

            List<Experience> miniBatch=agent.replayBuffer.getMiniBatch(miniBatchSize);
            if (miniBatch.size()==miniBatchSize) {
                DataSetIterator iterator = agent.createTrainingData(miniBatch);
                agent.network.fit(iterator);

                if (printFlag) {
                    //System.out.println("replayBuffer"+agent.replayBuffer);
                    //System.out.println("miniBatch"+miniBatch);
                }

            }

            sNew.copyState(stepReturn.state);
            agent.state.copyState(sNew);


          if (printFlag) {
                //System.out.println(agent.state+", aChosen:"+aChosen+", probRandAction:"+probRandAction);
                //System.out.println(stepReturn);
            }

            nofSteps++;
        } while (!stepReturn.termState & nofSteps<10);
    }


}
