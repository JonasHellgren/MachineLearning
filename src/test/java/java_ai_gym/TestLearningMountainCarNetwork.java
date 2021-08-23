package java_ai_gym;


import java_ai_gym.models_common.Experience;
import java_ai_gym.models_common.State;
import java_ai_gym.models_common.StepReturn;
import java_ai_gym.models_mountaincar.MountainCar;
import java_ai_gym.models_mountaincar.MountainCarAgentNeuralNetwork;
import java_ai_gym.swing.Position2D;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TestLearningMountainCarNetwork {

    private static final Logger logger = Logger.getLogger(TestLearningMountainCarNetwork.class.getName());


    State sNew = new State();
    MountainCar env = new MountainCar();
    MountainCarAgentNeuralNetwork agent = new MountainCarAgentNeuralNetwork(env.parameters);
    public final double SMALL = 0.001;
    private final Random random = new Random();
    private static final int NOF_EPISODES_BETWEEN_PRINTOUTS =10;
    private static final int NOF_EPISODES_BETWEEN_POLICY_TEST=10;

    @Test  @Ignore
    public void learnAtSameInputBigNegativeRewardZeroGamma() {

        agent.GAMMA=0;
        env.parameters.MAX_START_POSITION=env.parameters.POSITION_AT_MIN_HEIGHT;
        env.parameters.MIN_START_POSITION=env.parameters.POSITION_AT_MIN_HEIGHT;
        env.parameters.MIN_START_VELOCITY=0;
        env.parameters.MAX_START_VELOCITY=0;
        agent.MINI_BATCH_SIZE=10;

        for (int i = 0; i <agent.MINI_BATCH_SIZE ; i++) {
            env.initState(agent.state);
            int aChosen=agent.chooseRandomAction(env.parameters.discreteActionsSpace);
            //env.parameters.NON_TERMINAL_REWARD=(double) -100*aChosen;
            StepReturn stepReturn = env.step(aChosen, agent.state);
            stepReturn.reward=(double) -100*aChosen;  //mocking reward
            Experience experience = new Experience(new State(agent.state), aChosen, stepReturn);
            agent.replayBuffer.addExperience(experience);
        }

        List<Experience> miniBatch=agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(agent.MINI_BATCH_SIZE,0.5);

        /*System.out.println(agent.replayBuffer);    System.out.println(miniBatch);     System.out.println(agent.state); */

        for (int i = 0; i < 50 ; i++) {
            fitFromMiniBatch(miniBatch);
            printQsa();
        }

        Assert.assertEquals(0.0,agent.readMemory(agent.state,0,env.parameters),50);
        Assert.assertEquals(-100,agent.readMemory(agent.state,1,env.parameters),50);
        Assert.assertEquals(-200,agent.readMemory(agent.state,2,env.parameters),50);

    }


    @Test  @Ignore
    public void learnAtDifferentInputsZeroGamma() {

        agent.GAMMA=0;

        List<Double> positionsList = Arrays.asList(env.parameters.MIN_START_POSITION,0.0,env.parameters.MAX_START_POSITION/2);
        List<Double> velocitiesList = Arrays.asList(-env.parameters.MAX_SPEED,env.parameters.MAX_SPEED);

        //List<Double> positionsList = Arrays.asList(env.parameters.POSITION_AT_MIN_HEIGHT);
        //List<Double> velocitiesList = Arrays.asList(0.0);

        while (agent.replayBuffer.size()<agent.REPLAY_BUFFER_SIZE) {
        for (double pos:positionsList) {
            for (double vel:velocitiesList) {
                for (int a:env.parameters.discreteActionsSpace) {
                    //env.parameters.NON_TERMINAL_REWARD = calcMockReward(pos, vel, a);
                    agent.state.setVariable("position", pos);
                    agent.state.setVariable("velocity", vel);
                    agent.state.setVariable("nofSteps", 0);
                    StepReturn stepReturn = env.step(a, agent.state);
                    stepReturn.reward=calcMockReward(pos, vel, a);  //mocking reward
                    Experience experience = new Experience(new State(agent.state), a, stepReturn);
                    agent.replayBuffer.addExperience(experience);
                }
            }
       }
        }


        //System.out.println(agent.replayBuffer);
        // System.out.println(miniBatch);     System.out.println(agent.state);


        System.out.println("calcMockReward(pos, vel, 0):"+calcMockReward(positionsList.get(0), velocitiesList.get(0), 0));
        System.out.println("calcMockReward(pos, vel, 1):"+calcMockReward(positionsList.get(0), velocitiesList.get(0), 1));
        System.out.println("calcMockReward(pos, vel, 2):"+calcMockReward(positionsList.get(0), velocitiesList.get(0), 2));

        List<Experience> miniBatch=agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(agent.MINI_BATCH_SIZE,0.5);

        //System.out.println(miniBatch);

        for (int i = 0; i < 100 ; i++) {
           // System.out.println("i;"+i);
            miniBatch=agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(agent.MINI_BATCH_SIZE,0.5);
            System.out.println(miniBatch);
            fitFromMiniBatch(miniBatch);
            agent.state.setVariable("position",positionsList.get(0));
            agent.state.setVariable("velocity",velocitiesList.get(0));
            printQsa();
        }

        System.out.println("----------------");
        for (double pos:positionsList) {
            for (double vel:velocitiesList) {
                agent.state.setVariable("position",pos);
                agent.state.setVariable("velocity",vel);
                printAgentPositionAndState();
                System.out.println("calcMockReward"+
                        ", a=0:" +calcMockReward(pos,vel, 0)+
                        ", a=1:"+calcMockReward(pos,vel, 1)+
                        ", a=2:"+calcMockReward(pos,vel, 2));

                printQsa();
            }
        }

        double pos=positionsList.get(0);  double vel=velocitiesList.get(0);
        agent.state.setVariable("position",pos);
        agent.state.setVariable("velocity",vel);
        Assert.assertEquals(calcMockReward(pos, vel, 0),agent.readMemory(agent.state,0,env.parameters),10);
        Assert.assertEquals(calcMockReward(pos, vel, 1),agent.readMemory(agent.state,1,env.parameters),10);
        Assert.assertEquals(calcMockReward(pos, vel, 2),agent.readMemory(agent.state,2,env.parameters),10);

    }

    @Test  //@Ignore
    public void learnFromRandomInputsZeroGamma() throws InterruptedException {

        agent.GAMMA=0;

        List<Double> positionsList = Arrays.asList(env.parameters.MIN_START_POSITION,0.0,env.parameters.MAX_START_POSITION/2);
        List<Double> velocitiesList = Arrays.asList(-env.parameters.MAX_SPEED,env.parameters.MAX_SPEED);

        //List<Double> positionsList = Arrays.asList(env.parameters.POSITION_AT_MIN_HEIGHT);
        //List<Double> velocitiesList = Arrays.asList(0.0);

        while (agent.replayBuffer.size()<agent.REPLAY_BUFFER_SIZE) {
                    for (int a:env.parameters.discreteActionsSpace) {
                        env.setRandomStateValuesAny(agent.state);
                        StepReturn stepReturn = env.step(a, agent.state);
                        stepReturn.reward=calcRuleBasedReward(
                                agent.state.getContinuousVariable("position"),
                                agent.state.getContinuousVariable("velocity"),
                                a);  //mocking reward
                        Experience experience = new Experience(new State(agent.state), a, stepReturn);
                        agent.replayBuffer.addExperience(experience);
            }
        }

        testPolicy(100);
        for (int i = 0; i < 50 ; i++) {
            if (i % 10 ==0)
             System.out.println("i:"+i+"success ratio:"+testPolicy(100));

            List<Experience> miniBatch=agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(agent.MINI_BATCH_SIZE,0.5);
            fitFromMiniBatch(miniBatch);
        }


        List<Position2D> circlePositionList = new ArrayList<>();
        List<Integer> actionList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            env.setRandomStateValuesAny(agent.state);

            circlePositionList.add(new Position2D(
                    agent.state.getContinuousVariable("position"),
                    agent.state.getContinuousVariable("velocity")));
            actionList.add(agent.chooseBestAction(agent.state, env.parameters));

            printAgentPositionAndState();
            printQsa();
        }

        env.plotPanel.setCircleData(circlePositionList,actionList);
        env.plotPanel.repaint();
        TimeUnit.MILLISECONDS.sleep(10000);

    }

    private void printAgentPositionAndState() {
        System.out.println(
                "position:" + agent.state.getContinuousVariable("position") +
                        ", velocity:" + agent.state.getContinuousVariable("velocity"));
    }


    private double calcMockReward(double pos, double vel, int a) {
        return pos * 100 +
                vel * 100 +
                (double) 1 * a;
    }

    private double calcRuleBasedReward(double pos, double vel, int a) {
        int desiredAction=(vel <-0.001)?0:2;
        return (a==desiredAction)?1.0:0.0;
    }

    @Test @Ignore
    public void learnAtSameInputStandardRewardNonZeroGamma() {

        //env.parameters.NON_TERMINAL_REWARD=-10;
        agent.GAMMA=0.99;
        env.parameters.MAX_START_POSITION=env.parameters.POSITION_AT_MIN_HEIGHT;
        env.parameters.MIN_START_POSITION=env.parameters.POSITION_AT_MIN_HEIGHT;
        agent.MINI_BATCH_SIZE=10;

        for (int i = 0; i <agent.MINI_BATCH_SIZE ; i++) {
            env.initState(agent.state);
            int aChosen=agent.chooseRandomAction(env.parameters.discreteActionsSpace);
            //env.parameters.NON_TERMINAL_REWARD=(double) -1+0.1*aChosen;
            StepReturn stepReturn = env.step(aChosen, agent.state);
            stepReturn.reward=(double) -1+0.1*aChosen; //mocking reward
            Experience experience = new Experience(new State(agent.state), aChosen, stepReturn);
            agent.replayBuffer.addExperience(experience);
        }

        List<Experience> miniBatch=agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(agent.MINI_BATCH_SIZE,0.5);

        //System.out.println(agent.replayBuffer);   // System.out.println(miniBatch);     System.out.println(agent.state); */

        for (int i = 0; i < 1000 ; i++) {
            printQsa();
            fitFromMiniBatch(miniBatch);
            agent.addBellmanErrorItemForEpisodeAndClearPerStepList();
            printBellmanError(i,0,0);
        }

     /*   Assert.assertEquals(0.0,agent.readMemory(agent.state,0,env.parameters),10);
        Assert.assertEquals(-100,agent.readMemory(agent.state,1,env.parameters),10);
        Assert.assertEquals(-200,agent.readMemory(agent.state,2,env.parameters),10);  */

    }


    @Test  @Ignore
    //https://www.saashanair.com/dqn-code/
    public void runLearningTextBook() throws InterruptedException {
        // episode: a full iteration when the agent starts from a random state and finds the terminal state

        //logger.info("Nd4jBackend.BACKEND_PRIORITY_CPU: {}", Nd4jBackend.BACKEND_PRIORITY_CPU);
        //logger.info("Nd4jBackend.BACKEND_PRIORITY_GPU: {}", Nd4jBackend.BACKEND_PRIORITY_GPU);

        //env.PrintQsa(agent);
        for (int iEpisode = 0; iEpisode < agent.NUM_OF_EPISODES; ++iEpisode) {
            env.initState(agent.state);
            if (env.isTerminalState(agent.state)) continue;  // we do not want to start with the terminal state
            //System.out.println("Start state:");     System.out.println(agent.state);
            simulateTextBook(false,iEpisode);
        }

        animatePolicy();

        // env.PrintQsa(agent);
        System.out.println("nofFits:"+agent.nofFits+", nof steps:"+agent.state.nofSteps);
        System.out.println(agent.network.summary());
        //env.showPolicy(agent);

        Assert.assertTrue(agent.getBellmanErrorAverage(NOF_EPISODES_BETWEEN_PRINTOUTS)<0.05);

        agent.state.setVariable("position", env.parameters.POSITION_AT_MIN_HEIGHT+0.1);
        agent.state.setVariable("velocity", env.parameters.MAX_SPEED/10);
        Assert.assertEquals(2,agent.chooseBestAction(agent.state,env.parameters));

    }


    private void printQsa() {
        for (int aChosen : env.parameters.discreteActionsSpace)
            System.out.print("aChosen: " + aChosen + ", Q:" + agent.readMemory(agent.state, aChosen, env.parameters) + "; ");
        System.out.println();
    }

    private void printQsaAtPosBottomZeroSpeedAndRightPosSomeSpeed(int iEpisode) {

        if (iEpisode % NOF_EPISODES_BETWEEN_POLICY_TEST == 0 | iEpisode == 0) {
            agent.state.setVariable("position", env.parameters.POSITION_AT_MIN_HEIGHT);
            agent.state.setVariable("velocity", 0.0);

            for (int aChosen : env.parameters.discreteActionsSpace)
                System.out.print("PosBottomZeroSpeed aChosen: " + aChosen + ", Q:" + agent.readMemory(agent.state, aChosen, env.parameters) + "; ");
            System.out.println();

            agent.state.setVariable("position", env.parameters.MAX_START_POSITION);
            agent.state.setVariable("velocity", env.parameters.MAX_START_VELOCITY);

            for (int aChosen : env.parameters.discreteActionsSpace)
                System.out.print("RightPosSomeSpee aChosen: " + aChosen + ", Q:" + agent.readMemory(agent.state, aChosen, env.parameters) + "; ");
            System.out.println();

        }
    }



    private void printBellmanError(int iEpisode, int nofSteps, double maxPosition) {
        if (iEpisode % NOF_EPISODES_BETWEEN_PRINTOUTS == 0 | iEpisode == 0)
            System.out.println("iEpisode:"+ iEpisode +" bellmanError:"+
                    agent.getBellmanErrorAverage(NOF_EPISODES_BETWEEN_PRINTOUTS)+
                    ", nofSteps:"+nofSteps+
                    ", max position:"+maxPosition);
    }

    private int testPolicy(int nofTests) {

        List<Integer> nofStepsList = new ArrayList<>();
        int nofSuccessTests = 0;
        for (int i = 0; i < nofTests; i++) {
            env.setRandomStateValuesStart(agent.state);
            runPolicy();
            int nofSteps = agent.state.getDiscreteVariable("nofSteps");
            nofStepsList.add(nofSteps);
            if (nofSteps < env.parameters.MAX_NOF_STEPS)
                nofSuccessTests++;
        }
        return nofSuccessTests / nofTests;

    }

    private void runPolicy() {
        StepReturn stepReturn;
        do {
            stepReturn=env.step(agent.chooseBestAction(agent.state, env.parameters),agent.state);
            agent.state.copyState(stepReturn.state);
        } while (!stepReturn.termState);
    }


    private void simulateTextBook(boolean printFlag, int iEpisode) {
        // Q learning equation: Q[s][a] = Q[s][a] + alpha ( R[s][a] + gamma (max Q[sNew]) - Q[s][a] )
        // a single episode: the agent finds a path from state s to the exit state

        StepReturn stepReturn;
        double fEpisodes=(double) iEpisode/agent.NUM_OF_EPISODES;
        int nofSteps=0;  double maxPosition=-Double.MAX_VALUE;

        //System.out.println("bellmanErrorList size before"+agent.bellmanErrorList.size());


        do {
            int aChosen=agent.chooseAction(fEpisodes,env.parameters);
            stepReturn = env.step(aChosen, agent.state);
            Experience experience = new Experience(new State(agent.state), aChosen, stepReturn);
            agent.replayBuffer.addExperience(experience);

            if (agent.state.nofSteps % agent.NOF_STEPS_BETWEEN_FITS == 0) {
                List<Experience> miniBatch = agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(agent.MINI_BATCH_SIZE, fEpisodes);
                fitFromMiniBatch(miniBatch);
            }

            sNew.copyState(stepReturn.state);
            agent.state.copyState(sNew);

            if (env.isGoalState(stepReturn)) {
                System.out.println("Goal state reached");
                System.out.println(agent.state);
                System.out.println(stepReturn);
            }

            nofSteps++;  maxPosition=Math.max(maxPosition,agent.state.getContinuousVariable("position"));

        } while (!stepReturn.termState);

        //System.out.println(agent.bellmanErrorListItemPerStep);
        agent.addBellmanErrorItemForEpisodeAndClearPerStepList();
        printBellmanError(iEpisode,nofSteps,maxPosition);
        testPolicy(iEpisode);
        printQsaAtPosBottomZeroSpeedAndRightPosSomeSpeed(iEpisode);


        //System.out.println("nofSteps:"+nofSteps);
        //System.out.println("bellmanErrorList size:"+agent.bellmanErrorListItemPerEpisode.size()+", nofSteps:"+nofSteps+", max position:"+maxPosition);
        //System.out.println(agent.bellmanErrorList);
        //System.out.println(agent.replayBuffer);

    }

    private void fitFromMiniBatch(List<Experience> miniBatch) {
        if (miniBatch.size()== agent.MINI_BATCH_SIZE) {
            DataSetIterator iterator = agent.createTrainingData(miniBatch,env.parameters);
            agent.network.fit(iterator,agent.NUM_OF_EPOCHS);
        }
        else
            logger.warning("miniBatch.size() < agent.MINI_BATCH_SIZE");
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
