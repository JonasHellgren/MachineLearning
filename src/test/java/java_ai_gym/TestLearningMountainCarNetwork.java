package java_ai_gym;

import java_ai_gym.models_common.Experience;
import java_ai_gym.models_common.State;
import java_ai_gym.models_common.StepReturn;
import java_ai_gym.models_mountaincar.MountainCar;
import java_ai_gym.models_mountaincar.MountainCarAgentNeuralNetwork;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TestLearningMountainCarNetwork {

    State sNew = new State();
    MountainCar env = new MountainCar();
    MountainCarAgentNeuralNetwork agent = new MountainCarAgentNeuralNetwork(env.parameters);
    private static final int NOF_EPISODES_BETWEEN_PRINTOUTS =10;


    @Test
   // @Ignore
    //https://www.saashanair.com/dqn-code/
    public void runLearningTextBook() throws InterruptedException {
        // episode: a full iteration when the agent starts from a random state and finds the terminal state

        agent.GAMMA=0.99; //TODO remove

        //env.PrintQsa(agent);
        for (int iEpisode = 0; iEpisode < agent.NUM_OF_EPISODES; ++iEpisode) {
            env.initState(agent.state);
            System.out.println(agent.state);
            if (env.isTerminalState(agent.state)) continue;  // we do not want to start with the terminal state
            //System.out.println("Start state:");     System.out.println(agent.state);
            simulateTextBook(false, iEpisode);

            if (iEpisode % agent.NOF_EPISODES_BETWEEN_POLICY_TEST == 0 | iEpisode == 0)
                System.out.println("iEpisode:" + iEpisode + ", success ratio:" + env.testPolicy(agent.NOF_TESTS_WHEN_TESTING_POLICY,agent));

            System.out.println("------------------------------");


            agent.state.setVariable("position",env.parameters.MIN_START_POSITION/2);
            agent.state.setVariable("velocity",env.parameters.MIN_START_VELOCITY/2);
            agent.printQsa(env.parameters);

            agent.state.setVariable("position",env.parameters.MAX_START_POSITION/10);
            agent.state.setVariable("velocity",env.parameters.MAX_START_VELOCITY/10);
            agent.printQsa(env.parameters);

            agent.state.setVariable("position",env.parameters.MAX_START_POSITION/2);
            agent.state.setVariable("velocity",env.parameters.MAX_START_VELOCITY/2);
            agent.printQsa(env.parameters);




        }


        //animatePolicy();

        // env.PrintQsa(agent);
        System.out.println("nofFits:"+agent.nofFits+", totalNofSteps:"+agent.state.totalNofSteps);
        System.out.println(agent.network.summary());
        //env.showPolicy(agent);

        Assert.assertTrue(agent.getBellmanErrorAverage(NOF_EPISODES_BETWEEN_PRINTOUTS)<0.05);

        agent.state.setVariable("position", env.parameters.POSITION_AT_MIN_HEIGHT+0.1);
        agent.state.setVariable("velocity", env.parameters.MAX_SPEED/10);
        Assert.assertEquals(2,agent.chooseBestAction(agent.state,env.parameters));

    }


    private void simulateTextBook(boolean printFlag, int iEpisode) {
        // Q learning equation: Q[s][a] = Q[s][a] + alpha ( R[s][a] + gamma (max Q[sNew]) - Q[s][a] )
        // a single episode: the agent finds a path from state s to the exit state

        StepReturn stepReturn;
        double fEpisodes=(double) iEpisode/ (double) agent.NUM_OF_EPISODES;
        int nofSteps=0;  double maxPosition=-Double.MAX_VALUE;



        do {
            int aChosen=agent.chooseAction(fEpisodes,env.parameters);
            stepReturn = env.step(aChosen, agent.state);

            /*

            System.out.println("--------------");
            System.out.println(agent.state);
            System.out.println(stepReturn.state);
            System.out.println(stepReturn);  */


            Experience experience = new Experience(new State(agent.state), aChosen, stepReturn);
            agent.replayBuffer.addExperience(experience);

            //System.out.println("fEpisodes:"+fEpisodes);

            //System.out.println("agent.replayBuffer.size():"+agent.replayBuffer.size());
            if (agent.replayBuffer.isFull(agent)) {

                //System.out.println(agent.replayBuffer);
                if (agent.state.totalNofSteps % agent.NOF_STEPS_BETWEEN_FITS == 0) {
                    //System.out.println("fitting");
                    List<Experience> miniBatch =
                            agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(agent.MINI_BATCH_SIZE, 0.5);
                    agent.fitFromMiniBatch(miniBatch, env.parameters);
                    agent.maybeUpdateTargetNetwork();

                    //System.out.println(miniBatch);
                }
            }



            sNew.copyState(stepReturn.state);
            agent.state.copyState(sNew);

/*
            if (env.isGoalState(stepReturn)) {
                System.out.println("Goal state reached");
                System.out.println(agent.state);
                System.out.println(stepReturn);
            }  */

            nofSteps++;  maxPosition=Math.max(maxPosition,agent.state.getContinuousVariable("position"));

        } while (!stepReturn.termState);

        //System.out.println(agent.bellmanErrorListItemPerStep);
        agent.addBellmanErrorItemForEpisodeAndClearPerStepList();

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
            env.animationPanel.setCarStates(position,env.height(position),velocity);
            env.animationPanel.repaint();
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
