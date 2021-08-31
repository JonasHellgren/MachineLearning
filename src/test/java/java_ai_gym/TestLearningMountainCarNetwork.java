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

      /*  initNetwork();
        agent.replayBuffer.clear();
        agent.networkTarget.setParams(agent.network.params());  */

        agent.GAMMA=0.99; //TODO remove

        //env.PrintQsa(agent);
        for (int iEpisode = 0; iEpisode < agent.NUM_OF_EPISODES; ++iEpisode) {
            env.initState(agent.state);
            System.out.println(agent.state);
            if (env.isTerminalState(agent.state)) continue;  // we do not want to start with the terminal state
            //System.out.println("Start state:");     System.out.println(agent.state);
            simulateTextBook(false, iEpisode);


            System.out.println("------------------------------");
            if (iEpisode % agent.NOF_EPISODES_BETWEEN_POLICY_TEST == 0 | iEpisode == 0)
                System.out.println("iEpisode:" + iEpisode +
                        ", success ratio:" + env.testPolicy(agent.NOF_TESTS_WHEN_TESTING_POLICY,agent)+
                        ", tbd:" + 0.0);



            printStates();


        }


        animatePolicy();

        // env.PrintQsa(agent);
        System.out.println("nofFits:"+agent.nofFits+", totalNofSteps:"+agent.state.totalNofSteps);
        System.out.println(agent.network.summary());
        //env.showPolicy(agent);

        Assert.assertTrue(env.testPolicy(agent.NOF_TESTS_WHEN_TESTING_POLICY,agent)>0.9);



    }

    private void printStates() {
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


    private void simulateTextBook(boolean printFlag, int iEpisode) {
        // Q learning equation: Q[s][a] = Q[s][a] + alpha ( R[s][a] + gamma (max Q[sNew]) - Q[s][a] )
        // a single episode: the agent finds a path from state s to the exit state

        StepReturn stepReturn;
        double fEpisodes=(double) iEpisode/ (double) agent.NUM_OF_EPISODES;
        int nofSteps=0;  double maxPosition=-Double.MAX_VALUE;



        do {
            int aChosen=agent.chooseAction(fEpisodes,env.parameters);
            stepReturn = env.step(aChosen, agent.state);


            Experience experience = new Experience(new State(agent.state), aChosen, stepReturn, agent.BE_ERROR_INIT);
            agent.replayBuffer.addExperience(experience);

            //System.out.println("fEpisodes:"+fEpisodes);

            //System.out.println("agent.replayBuffer.size():"+agent.replayBuffer.size());
            if (agent.replayBuffer.isFull(agent) & agent.isTimeToFit() ) {
                    //System.out.println("fitting");
                    List<Experience> miniBatch =
                            agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(agent.MINI_BATCH_SIZE, 0.5);
                    agent.fitFromMiniBatch(miniBatch, env.parameters,fEpisodes);
                    agent.maybeUpdateTargetNetwork();

                    //System.out.println(miniBatch);

            }



            sNew.copyState(stepReturn.state);
            agent.state.copyState(sNew);

/*
            if (env.isGoalState(stepReturn)) {
                System.out.println("Goal state reached");
                System.out.println(agent.state);
                System.out.println(stepReturn);
            }  */

            env.render(agent,aChosen);
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

      //      double position=agent.state.getContinuousVariable("position");
       //     double velocity=agent.state.getContinuousVariable("velocity");

            int aBest=agent.chooseBestAction(agent.state, env.parameters);
            stepReturn=env.step(aBest,agent.state);
            agent.state.copyState(stepReturn.state);

            env.render(agent,aBest);
            TimeUnit.MILLISECONDS.sleep(100);

            if (env.isGoalState(stepReturn)) {
                System.out.println("Goal state reached");
                System.out.println(agent.state);
                System.out.println(stepReturn);
            }

        } while (!stepReturn.termState);

        TimeUnit.MILLISECONDS.sleep(1000);

    }

    public void initNetwork() {
        agent.GAMMA=0;

        while (agent.replayBuffer.size()<agent.REPLAY_BUFFER_SIZE) {
            for (int a:env.parameters.discreteActionsSpace) {
                env.setRandomStateValuesAny(agent.state);
                StepReturn stepReturn = env.step(a, agent.state);
                stepReturn.reward=-20.0;
                Experience experience = new Experience(new State(agent.state), a, stepReturn,agent.BE_ERROR_INIT);
                agent.replayBuffer.addExperience(experience);
            }
        }


        int nofTests=100;        env.testPolicy(nofTests,agent);
        for (int i = 0; i < 50 ; i++) {
            if (i % 10 ==0) {
                System.out.println("i:" + i + "success ratio:" + env.testPolicy(nofTests, agent));
                agent.state.setVariable("position", env.parameters.MAX_START_POSITION/2);
                agent.state.setVariable("velocity", env.parameters.MAX_SPEED/2);
                agent.printQsa(env.parameters);
            }

            List<Experience> miniBatch=agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(agent.MINI_BATCH_SIZE,1);
            agent.fitFromMiniBatch(miniBatch,env.parameters,0);

        }


    }

}
