package java_ai_gym;

import java_ai_gym.models_common.Environment;
import java_ai_gym.models_common.Experience;
import java_ai_gym.models_common.State;
import java_ai_gym.models_common.StepReturn;
import java_ai_gym.models_mountaincar.MountainCar;
import java_ai_gym.models_mountaincar.MountainCarAgentNeuralNetwork;
import java_ai_gym.models_sixrooms.SixRoomsAgentNeuralNetwork;
import java_ai_gym.swing.Position2D;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TestLearningMountainCarNetwork {

    private static final Logger logger = Logger.getLogger(TestLearningMountainCarNetwork.class.getName());
    State sNew = new State();
    MountainCar env = new MountainCar();
    MountainCarAgentNeuralNetwork agent = new MountainCarAgentNeuralNetwork(env.parameters,env.getTemplateState());
    private static final int NOF_EPISODES_BETWEEN_PRINTOUTS = 10;



    @Test
    //https://www.saashanair.com/dqn-code/
    public void runLearningTextBook() throws InterruptedException {
        // episode: a full iteration when the agent starts from a random state and finds the terminal state

        plotPolicy();
        for (int iEpisode = 0; iEpisode < agent.NUM_OF_EPISODES; ++iEpisode) {
            env.setRandomStateValuesAny(agent.state);
            env.simulateEpisode(agent, iEpisode, env, env.parameters);

            List<Experience> miniBatch =
                    agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(
                            agent.MINI_BATCH_SIZE,
                            agent.calcFractionEpisodes(iEpisode));
            plotMiniBatch(miniBatch);

            //System.out.println("replayBuffer.size:"+agent.replayBuffer.size()+", totalNofSteps:"+agent.state.totalNofSteps);

            if (env.isTimeForPolicyTest(iEpisode)) {
                Environment.PolicyTestReturn policyTestReturn = env.testPolicy(agent, env.parameters, env.NOF_TESTS_WHEN_TESTING_POLICY);
                env.printPolicyTest(iEpisode, agent, policyTestReturn, env.parameters.MAX_NOF_STEPS_POLICY_TEST);
            }
        }

        plotPolicy();
        TimeUnit.MILLISECONDS.sleep(1000);
        animatePolicy();

        System.out.println("nofFits:"+agent.nofFits+", totalNofSteps:"+agent.state.totalNofSteps);
        System.out.println(agent.network.summary());
        Assert.assertTrue(env.testPolicy(agent, env.parameters, env.NOF_TESTS_WHEN_TESTING_POLICY).successRatio>0.8);

    }


    public void animatePolicy() throws InterruptedException {
        env.setRandomStateValuesStart(agent.state);
        System.out.println(agent.state);
        StepReturn stepReturn;

        do {

            int aBest=agent.chooseBestAction(agent.state, env.parameters);
            stepReturn=env.step(aBest,agent.state);
            agent.state.copyState(stepReturn.state);

            env.render(agent.state, agent.findMaxQTargetNetwork(agent.state,env.parameters),aBest);
            TimeUnit.MILLISECONDS.sleep(100);

            if (env.isGoalState(stepReturn)) {
                System.out.println("Goal state reached");
                System.out.println(agent.state);
                System.out.println(stepReturn);
            }

        } while (!stepReturn.termState);

        TimeUnit.MILLISECONDS.sleep(1000);
    }

    public void plotPolicy() {
        List<Position2D> circlePositionList = new ArrayList<>();
        List<Integer> actionList = new ArrayList<>();
        for (int i = 0; i < env.gfxSettings.NOF_DOTS_PLOTTED_POLICY; i++) {
            env.setRandomStateValuesAny(agent.state);
            double pos=agent.state.getContinuousVariable("position");
            double vel=agent.state.getContinuousVariable("velocity");
            circlePositionList.add(new Position2D(pos,vel));
            actionList.add(agent.chooseBestAction(agent.state, env.parameters));
        }
        env.plotPanel.setCircleData(circlePositionList,actionList);
        env.plotPanel.repaint();
    }

    public void plotMiniBatch(List<Experience> miniBatch) {
        List<Position2D> circlePositionList = new ArrayList<>();
        List<Integer> actionList = new ArrayList<>();
        for (Experience exp: miniBatch) {
            double pos=exp.s.getContinuousVariable("position");
            double vel=exp.s.getContinuousVariable("velocity");
            circlePositionList.add(new Position2D(pos,vel));
            actionList.add(exp.action);
        }
        env.plotPanel.setCircleData(circlePositionList,actionList);
        env.plotPanel.repaint();
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

      env.testPolicy(agent, env.parameters, env.NOF_TESTS_WHEN_TESTING_POLICY);
        for (int i = 0; i < 50 ; i++) {
            if (i % 10 ==0) {
                System.out.println("i:" + i + "success ratio:" + env.testPolicy(agent, env.parameters, env.NOF_TESTS_WHEN_TESTING_POLICY));
                agent.state.setVariable("position", env.parameters.MAX_START_POSITION/2);
                agent.state.setVariable("velocity", env.parameters.MAX_SPEED/2);
                agent.printQsa(env.parameters);
            }

            List<Experience> miniBatch=agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(agent.MINI_BATCH_SIZE,1);
            agent.fitFromMiniBatch(miniBatch,env.parameters,0);

        }
    }

}
