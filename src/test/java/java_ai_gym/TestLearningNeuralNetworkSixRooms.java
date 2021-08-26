package java_ai_gym;


import java_ai_gym.models_common.Experience;
import java_ai_gym.models_common.AgentNeuralNetwork;
import java_ai_gym.models_common.State;
import java_ai_gym.models_common.StepReturn;
import java_ai_gym.models_sixrooms.SixRooms;
//import java_ai_gym.temp.SixRoomsAgentNeuralNetwork;
import java_ai_gym.models_sixrooms.SixRoomsAgentNeuralNetwork;
import org.jcodec.common.Assert;
import org.junit.Test;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4jBackend;


import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class TestLearningNeuralNetworkSixRooms {
    private static final Logger logger = Logger.getLogger(TestLearningNeuralNetworkSixRooms.class.getName());
    State sNew = new State();
    SixRooms env = new SixRooms();
    SixRoomsAgentNeuralNetwork agent = new SixRoomsAgentNeuralNetwork(env.parameters);
    public final double SMALL = 0.001;
    private final Random random = new Random();
    private static final int NOF_EPISODES_BETWEEN_PRINTOUTS =50;

    @Test
    //https://www.saashanair.com/dqn-code/
    public void runLearningTextBook() {
        // episode: a full iteration when the agent starts from a random state and finds the terminal state

        //logger.info("Nd4jBackend.BACKEND_PRIORITY_CPU: {}", Nd4jBackend.BACKEND_PRIORITY_CPU);
        //logger.info("Nd4jBackend.BACKEND_PRIORITY_GPU: {}", Nd4jBackend.BACKEND_PRIORITY_GPU);

        env.PrintQsa(agent);
        for (int iEpisode = 0; iEpisode < agent.NUM_OF_EPISODES; ++iEpisode) {

            int startState = random.nextInt(env.parameters.nofStates);
            agent.state.setVariable("roomNumber", startState);
            if (env.isTerminalState(agent.state)) continue;  // we do not want to start with the terminal state

            simulateTextBook(false,iEpisode);
        }

        env.PrintQsa(agent);
        System.out.println("nofFits:"+agent.nofFits);
        System.out.println(agent.network.summary());
        env.showPolicy(agent);

        Assert.assertTrue(agent.getBellmanErrorAverage(NOF_EPISODES_BETWEEN_PRINTOUTS)<0.1);

        agent.state.setVariable("roomNumber", 0);
        Assert.assertEquals(4,agent.chooseBestAction(agent.state,env.parameters));

        agent.state.setVariable("roomNumber", 1);
        Assert.assertEquals(5,agent.chooseBestAction(agent.state,env.parameters));

        agent.state.setVariable("roomNumber", 2);
        Assert.assertEquals(3,agent.chooseBestAction(agent.state,env.parameters));

        agent.state.setVariable("roomNumber", 4);
        Assert.assertEquals(5,agent.chooseBestAction(agent.state,env.parameters));



    }

    private void printBellmanError(int iEpisode) {
        if (iEpisode % NOF_EPISODES_BETWEEN_PRINTOUTS == 0 | iEpisode == 0)
            System.out.println("iEpisode:"+ iEpisode +" bellmanError:"+
                    agent.getBellmanErrorAverage(NOF_EPISODES_BETWEEN_PRINTOUTS)
            );
    }

    private void simulateTextBook(boolean printFlag, int iEpisode) {
        // Q learning equation: Q[s][a] = Q[s][a] + alpha ( R[s][a] + gamma (max Q[sNew]) - Q[s][a] )
        // a single episode: the agent finds a path from state s to the exit state

        StepReturn stepReturn;
        int miniBatchSize= agent.MINI_BATCH_SIZE;
        double fEpisodes=(double) iEpisode/agent.NUM_OF_EPISODES;
        int nofSteps=0;

        do {
            int aChosen=agent.chooseAction(fEpisodes,env.parameters);
            stepReturn = env.step(aChosen, agent.state);
            Experience experience = new Experience(new State(agent.state), aChosen, stepReturn);
            agent.replayBuffer.addExperience(experience);

            List<Experience> miniBatch=agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(miniBatchSize,fEpisodes);

            if (miniBatch.size()==miniBatchSize) {

                DataSetIterator iterator = agent.createTrainingData(miniBatch,env.parameters);
                agent.network.fit(iterator,agent.NUM_OF_EPOCHS);

                if (printFlag) {
                    System.out.println("replayBuffer"+agent.replayBuffer);
                    System.out.println("miniBatch"+miniBatch);
                }

            }

            sNew.copyState(stepReturn.state);
            agent.state.copyState(sNew);

            nofSteps++;
        } while (!stepReturn.termState & nofSteps<10);

        agent.addBellmanErrorItemForEpisodeAndClearPerStepList();
        printBellmanError(iEpisode);
    }


}
