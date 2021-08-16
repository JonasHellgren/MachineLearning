package java_ai_gym;


import java_ai_gym.models_common.Experience;
import java_ai_gym.models_common.State;
import java_ai_gym.models_common.StepReturn;
import java_ai_gym.models_sixrooms.SixRooms;
//import java_ai_gym.temp.SixRoomsAgentNeuralNetwork;
import org.jcodec.common.Assert;
import org.junit.Test;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_sixrooms.SixRoomsAgentNeuralNetwork;


import java.util.List;
import java.util.Random;

public class TestLearningNeuralNetworkSixRooms {
    private static final Logger logger = LoggerFactory.getLogger(TestLearningNeuralNetworkSixRooms.class);

    /*

    State sNew = new State();
    SixRooms env = new SixRooms();
    SixRoomsAgentNeuralNetwork agent = new SixRoomsAgentNeuralNetwork(env.parameters);
    public final double SMALL = 0.001;
    private final Random random = new Random();
    private static final int NOF_EPISODES_BETWEEN_PRINTOUTS =100;

    @Test
    //https://www.saashanair.com/dqn-code/
    public void runLearningTextBook() {
        // episode: a full iteration when the agent starts from a random state and finds the terminal state

        logger.info("Nd4jBackend.BACKEND_PRIORITY_CPU: {}", Nd4jBackend.BACKEND_PRIORITY_CPU);
        logger.info("Nd4jBackend.BACKEND_PRIORITY_GPU: {}", Nd4jBackend.BACKEND_PRIORITY_GPU);

        env.PrintQsa(agent);
        for (int iEpisode = 0; iEpisode < agent.NUM_OF_EPISODES; ++iEpisode) {

            if (iEpisode  % NOF_EPISODES_BETWEEN_PRINTOUTS == 0 & iEpisode>0)
                System.out.println("iEpisode:"+iEpisode+" bellmanError:"+agent.getBellmanErrorAverage(NOF_EPISODES_BETWEEN_PRINTOUTS));
            int startState = random.nextInt(env.parameters.nofStates);
            agent.state.setVariable("roomNumber", startState);
            if (env.isTerminalState(agent.state)) continue;  // we do not want to start with the terminal state

            simulateTextBook(false,(double) iEpisode/agent.NUM_OF_EPISODES);
        }

        env.PrintQsa(agent);
        System.out.println("nofFits:"+agent.nofFits);
        System.out.println(agent.network.summary());
        env.showPolicy(agent);

        Assert.assertTrue(agent.getBellmanErrorAverage(NOF_EPISODES_BETWEEN_PRINTOUTS)<0.05);

        agent.state.setVariable("roomNumber", 0);
        Assert.assertEquals(4,agent.chooseBestAction(agent.state));

        agent.state.setVariable("roomNumber", 1);
        Assert.assertEquals(5,agent.chooseBestAction(agent.state));

        agent.state.setVariable("roomNumber", 2);
        Assert.assertEquals(3,agent.chooseBestAction(agent.state));

        agent.state.setVariable("roomNumber", 4);
        Assert.assertEquals(5,agent.chooseBestAction(agent.state));



    }

    private void simulateTextBook(boolean printFlag, double fEpisodes) {
        // Q learning equation: Q[s][a] = Q[s][a] + alpha ( R[s][a] + gamma (max Q[sNew]) - Q[s][a] )
        // a single episode: the agent finds a path from state s to the exit state

        StepReturn stepReturn;
        int miniBatchSize=SixRoomsAgentNeuralNetwork.MINI_BATCH_SIZE;
        int nofSteps=0;

        do {
            int aChosen=agent.chooseAction(fEpisodes);
            stepReturn = env.step(aChosen, agent.state);
            Experience experience = new Experience(new State(agent.state), aChosen, stepReturn);
            agent.replayBuffer.addExperience(experience, agent.REPLAY_BUFFER_SIZE);

            List<Experience> miniBatch=agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(miniBatchSize,agent,fEpisodes);

            if (miniBatch.size()==miniBatchSize) {

                DataSetIterator iterator = agent.createTrainingData(miniBatch);
                agent.network.fit(iterator);

                if (printFlag) {
                    System.out.println("replayBuffer"+agent.replayBuffer);
                    System.out.println("miniBatch"+miniBatch);
                }

            }

            sNew.copyState(stepReturn.state);
            agent.state.copyState(sNew);

            nofSteps++;
        } while (!stepReturn.termState & nofSteps<10);
    }

*/
}
