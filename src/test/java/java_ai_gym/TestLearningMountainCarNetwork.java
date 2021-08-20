package java_ai_gym;

import java_ai_gym.models_common.Experience;
import java_ai_gym.models_common.State;
import java_ai_gym.models_common.StepReturn;
import java_ai_gym.models_mountaincar.MountainCar;
import java_ai_gym.models_mountaincar.MountainCarAgentNeuralNetwork;
import java_ai_gym.models_sixrooms.SixRooms;
import java_ai_gym.models_sixrooms.SixRoomsAgentNeuralNetwork;
import org.jcodec.common.Assert;
import org.junit.Test;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TestLearningMountainCarNetwork {

    State sNew = new State();
    MountainCar env = new MountainCar();
    MountainCarAgentNeuralNetwork agent = new MountainCarAgentNeuralNetwork(env.parameters);
    public final double SMALL = 0.001;
    private final Random random = new Random();
    private static final int NOF_EPISODES_BETWEEN_PRINTOUTS =1;


    @Test
    //https://www.saashanair.com/dqn-code/
    public void runLearningTextBook() throws InterruptedException {
        // episode: a full iteration when the agent starts from a random state and finds the terminal state

        //logger.info("Nd4jBackend.BACKEND_PRIORITY_CPU: {}", Nd4jBackend.BACKEND_PRIORITY_CPU);
        //logger.info("Nd4jBackend.BACKEND_PRIORITY_GPU: {}", Nd4jBackend.BACKEND_PRIORITY_GPU);

        //env.PrintQsa(agent);
        for (int iEpisode = 0; iEpisode < agent.NUM_OF_EPISODES; ++iEpisode) {

            printBellmanError(iEpisode);

            env.initState(agent.state);

            if (env.isTerminalState(agent.state)) continue;  // we do not want to start with the terminal state

            //System.out.println("Start state:");     System.out.println(agent.state);

            simulateTextBook(false,(double) iEpisode/agent.NUM_OF_EPISODES);
        }

        animatePolicy();

        // env.PrintQsa(agent);
        System.out.println("nofFits:"+agent.nofFits);
        System.out.println(agent.network.summary());
        //env.showPolicy(agent);

        Assert.assertTrue(agent.getBellmanErrorAverage(NOF_EPISODES_BETWEEN_PRINTOUTS)<0.05);

        agent.state.setVariable("position", env.parameters.POSITION_AT_MIN_HEIGHT+0.1);
        agent.state.setVariable("velocity", env.parameters.MAX_SPEED/10);
        Assert.assertEquals(2,agent.chooseBestAction(agent.state,env.parameters));

    }

    private void printBellmanError(int iEpisode) {
        if (iEpisode % NOF_EPISODES_BETWEEN_PRINTOUTS == 0 & iEpisode >0)
            System.out.println("iEpisode:"+ iEpisode +" bellmanError:"+agent.getBellmanErrorAverage(NOF_EPISODES_BETWEEN_PRINTOUTS));
    }

    private void simulateTextBook(boolean printFlag, double fEpisodes) {
        // Q learning equation: Q[s][a] = Q[s][a] + alpha ( R[s][a] + gamma (max Q[sNew]) - Q[s][a] )
        // a single episode: the agent finds a path from state s to the exit state

        StepReturn stepReturn;
        int miniBatchSize= agent.MINI_BATCH_SIZE;
        int nofSteps=0;

        do {
            int aChosen=agent.chooseAction(fEpisodes,env.parameters);
            stepReturn = env.step(aChosen, agent.state);
            Experience experience = new Experience(new State(agent.state), aChosen, stepReturn);
            agent.replayBuffer.addExperience(experience);
            List<Experience> miniBatch=agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(miniBatchSize,fEpisodes);

            if (miniBatch.size()==miniBatchSize) {
                DataSetIterator iterator = agent.createTrainingData(miniBatch,env.parameters);
                agent.network.fit(iterator);
                if (printFlag) {
                    System.out.println("replayBuffer"+agent.replayBuffer);
                    System.out.println("miniBatch"+miniBatch);
                }
            }

            sNew.copyState(stepReturn.state);
            agent.state.copyState(sNew);

            if (env.isGoalState(stepReturn)) {
                System.out.println("Goal state reached");
                System.out.println(agent.state);
                System.out.println(stepReturn);
            }

            nofSteps++;
        } while (!stepReturn.termState);
    }


    public void animatePolicy() throws InterruptedException {

        env.initState(agent.state);
        System.out.println(agent.state);
        StepReturn stepReturn;

        do {

            double position=agent.state.getContinuousVariable("position");
            double velocity=agent.state.getContinuousVariable("velocity");

            stepReturn=env.step(agent.chooseBestAction(agent.state, env.parameters),agent.state);
            agent.state.copyState(stepReturn.state);

            System.out.println(agent.state);
            env.panel.setCarStates(position,env.height(position),velocity);
            env.panel.repaint();
            TimeUnit.MILLISECONDS.sleep(100);

            if (env.isGoalState(stepReturn)) {
                System.out.println("Goal state reached");
                System.out.println(agent.state);
                System.out.println(stepReturn);
            }

        } while (!stepReturn.termState);

        TimeUnit.MILLISECONDS.sleep(1000);

    }


}
