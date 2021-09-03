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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/*** These tests focus on the function approximator of the agent, i.e. training of neural network.
 * By "mocking" of the reward tests become standalone, independent of the actual reward function.
 *
 */

public class TestLFunctionApproximationMountainCarNetwork {

    private static final Logger logger = Logger.getLogger(TestLFunctionApproximationMountainCarNetwork.class.getName());

    MountainCar env = new MountainCar();
    MountainCarAgentNeuralNetwork agent = new MountainCarAgentNeuralNetwork(env.parameters);
    private static final int NOF_EPISODES_BETWEEN_PRINTOUTS =10;


    @Test  @Ignore
    public void learnAtSameInputBigNegativeRewardZeroGamma() {

        agent.GAMMA=0;
        env.parameters.MAX_START_POSITION=env.parameters.POSITION_AT_MIN_HEIGHT;
        env.parameters.MIN_START_POSITION=env.parameters.POSITION_AT_MIN_HEIGHT;
        env.parameters.MIN_START_VELOCITY=0;
        env.parameters.MAX_START_VELOCITY=0;
        agent.MINI_BATCH_SIZE=10;

      while (agent.replayBuffer.size()<agent.REPLAY_BUFFER_SIZE) {
            //env.initState(agent.state);
            env.setRandomStateValuesAny(agent.state);
            int aChosen=agent.chooseRandomAction(env.parameters.discreteActionsSpace);
            StepReturn stepReturn = env.step(aChosen, agent.state);
            stepReturn.reward=(double) -100*aChosen;  //mocking reward
            Experience experience = new Experience(new State(agent.state), aChosen, stepReturn,agent.BE_ERROR_INIT);
            agent.replayBuffer.addExperience(experience);
        }

        List<Experience> miniBatch=agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(agent.MINI_BATCH_SIZE,0.5);
        for (int i = 0; i < 50 ; i++) {
            agent.fitFromMiniBatch(miniBatch,env.parameters,1);
            agent.printQsa(env.parameters);
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

        while (agent.replayBuffer.size()<agent.REPLAY_BUFFER_SIZE) {
        for (double pos:positionsList) {
            for (double vel:velocitiesList) {
                for (int a:env.parameters.discreteActionsSpace) {
                    agent.state.setVariable("position", pos);
                    agent.state.setVariable("velocity", vel);
                    agent.state.setVariable("nofSteps", 0);
                    StepReturn stepReturn = env.step(a, agent.state);
                    stepReturn.reward=calcMockReward(pos, vel, a);  //mocking reward
                    Experience experience = new Experience(new State(agent.state), a, stepReturn,agent.BE_ERROR_INIT);
                    agent.replayBuffer.addExperience(experience);
                }
            }
       }
        }

        System.out.println("calcMockReward(pos, vel, 0):"+calcMockReward(positionsList.get(0), velocitiesList.get(0), 0));
        System.out.println("calcMockReward(pos, vel, 1):"+calcMockReward(positionsList.get(0), velocitiesList.get(0), 1));
        System.out.println("calcMockReward(pos, vel, 2):"+calcMockReward(positionsList.get(0), velocitiesList.get(0), 2));


        for (int i = 0; i < 100 ; i++) {
            List<Experience>  miniBatch=agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(agent.MINI_BATCH_SIZE,0.5);
            agent.fitFromMiniBatch(miniBatch,env.parameters,1);
            agent.state.setVariable("position",positionsList.get(0));
            agent.state.setVariable("velocity",velocitiesList.get(0));
        }

        System.out.println("----------------");
        for (double pos:positionsList) {
            for (double vel:velocitiesList) {
                agent.state.setVariable("position",pos);
                agent.state.setVariable("velocity",vel);
                agent.printPositionAndVelocity();
                System.out.println("calcMockReward"+
                        ", a=0:" +calcMockReward(pos,vel, 0)+
                        ", a=1:"+calcMockReward(pos,vel, 1)+
                        ", a=2:"+calcMockReward(pos,vel, 2));
                agent.printQsa(env.parameters);
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

        while (agent.replayBuffer.size()<agent.REPLAY_BUFFER_SIZE) {
                    for (int a:env.parameters.discreteActionsSpace) {
                        env.setRandomStateValuesAny(agent.state);
                        StepReturn stepReturn = env.step(a, agent.state);
                        stepReturn.reward=calcRuleBasedReward(
                                agent.state.getContinuousVariable("position"),
                                agent.state.getContinuousVariable("velocity"),
                                a);  //mocking reward
                        Experience experience = new Experience(new State(agent.state), a, stepReturn,agent.BE_ERROR_INIT);
                        agent.replayBuffer.addExperience(experience);
            }
        }


        int nofTests=100;        env.testPolicy(nofTests,agent);
        int nofEpis=100;
        for (int i = 0; i < nofEpis ; i++) {
            if (i % 10 ==0) {
                System.out.println("i:" + i + "success ratio:" + env.testPolicy(nofTests, agent));

                agent.state.setVariable("position", env.parameters.MAX_START_POSITION/2);
                agent.state.setVariable("velocity", env.parameters.MAX_SPEED/2);
                agent.printQsa(env.parameters);
            }

            List<Experience> miniBatch=agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(agent.MINI_BATCH_SIZE,1);
            double fEpis=(double) i/ (double) nofEpis;
            agent.fitFromMiniBatch(miniBatch,env.parameters,fEpis);


        }

        System.out.println("state:"+agent.state);
        System.out.println("nofFits:"+agent.nofFits+", totalNofSteps:"+agent.state.totalNofSteps);


        List<Position2D> circlePositionList = new ArrayList<>();
        List<Integer> actionList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            //env.setRandomStateValuesAny(agent.state);
            env.setRandomStateValuesStart(agent.state);

            double pos=agent.state.getContinuousVariable("position");
            double vel=agent.state.getContinuousVariable("velocity");
            circlePositionList.add(new Position2D(pos,vel));
            actionList.add(agent.chooseBestAction(agent.state, env.parameters));

            agent.printPositionAndVelocity();
            agent.printQsa(env.parameters);

            for (int a : env.parameters.discreteActionsSpace)
                Assert.assertEquals(calcRuleBasedReward(pos, vel, a),
                        agent.readMemory(agent.state, a, env.parameters),
                        0.5);

        }

        env.plotPanel.setCircleData(circlePositionList,actionList);
        env.plotPanel.repaint();
        TimeUnit.MILLISECONDS.sleep(10000);

    }

    @Test   @Ignore
    public void learnAtSameInputStandardRewardNonZeroGamma() {

        agent.GAMMA=0.99;


        while (agent.replayBuffer.size()<agent.REPLAY_BUFFER_SIZE) {
            for (int a:env.parameters.discreteActionsSpace) {
                env.setRandomStateValuesAny(agent.state);
                StepReturn stepReturn = env.step(a, agent.state);
                stepReturn.reward=-1.0;
                Experience experience = new Experience(new State(agent.state), a, stepReturn, agent.BE_ERROR_INIT);
                agent.replayBuffer.addExperience(experience);
            }
        }

        List<Experience> miniBatch=agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(agent.MINI_BATCH_SIZE,0.5);

        for (int i = 0; i < 500; i++) {
            agent.printQsa(env.parameters);
            agent.fitFromMiniBatch(miniBatch,env.parameters,1);
            if (i % agent.NOF_FITS_BETWEEN_TARGET_NETWORK_UPDATE == 0)
                agent.networkTarget.setParams(agent.network.params());

            agent.addBellmanErrorItemForEpisodeAndClearPerStepList();
            printBellmanError(i, 0, 0);
        }

       Assert.assertTrue(agent.readMemory(agent.state,0,env.parameters) < -10);

    }




    private double calcMockReward(double pos, double vel, int a) {
        return pos * 100 +
                vel * 100 +
                (double) 1 * a;
    }

    private double calcRuleBasedReward(double pos, double vel, int a) {
        int desiredAction=(vel <-0.001)?0:2;
        double rAction=(a==desiredAction)?1.0:0.0;
        return -30*1+rAction;
    }


    private void printBellmanError(int iEpisode, int nofSteps, double maxPosition) {
        if (iEpisode % NOF_EPISODES_BETWEEN_PRINTOUTS == 0 | iEpisode == 0)
            System.out.println("iEpisode:"+ iEpisode +" bellmanError:"+
                    agent.getBellmanErrorAverage(NOF_EPISODES_BETWEEN_PRINTOUTS)+
                    ", nofSteps:"+nofSteps+
                    ", max position:"+maxPosition);
    }


}
