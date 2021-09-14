package java_ai_gym;

import java_ai_gym.models_common.Environment;
import java_ai_gym.models_common.Experience;
import java_ai_gym.models_common.State;
import java_ai_gym.models_poleoncart.CartPole;
import org.deeplearning4j.util.ModelSerializer;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TestLearningCartPoleNetwork {

    private static final Logger logger = Logger.getLogger(TestLearningCartPoleNetwork.class.getName());
    State sNew = new State();
    CartPole env = new CartPole();
    CartPoleAgentNeuralNetwork agent = new CartPoleAgentNeuralNetwork(env.parameters,env.getTemplateState());
    private static final int NOF_EPISODES_BETWEEN_PRINTOUTS = 10;

    File polePolicyInit = new File("c:/temp/polePolicyInit");
    File polePolicyInitTarget = new File("c:/temp/polePolicyInitTarget");
    File polePolicy = new File("c:/temp/polePolicy");
    File polePolicyTarget = new File("c:/temp/polePolicyTarget");


    @Test @Ignore
    //https://www.saashanair.com/dqn-code/
    public void runLearningTextBook() throws InterruptedException, IOException {
        // episode: a full iteration when the agent starts from a random state and finds the terminal state

        //agent.GAMMA=0;

        //plotPolicy();
        logger.info("Init policy found and saved");
        agent.network.save(polePolicyInit);
        agent.networkTarget.save(polePolicyInitTarget);
        double bestNofSteps=0;
        for (int iEpisode = 0; iEpisode <= agent.NUM_OF_EPISODES; ++iEpisode) {
            env.setRandomStateValuesStart(agent.state);
            env.simulateEpisode(agent, iEpisode, env, env.parameters);

            List<Experience> miniBatch =
                    agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(
                            agent.MINI_BATCH_SIZE,
                            agent.calcFractionEpisodes(iEpisode));
            //plotMiniBatch(miniBatch);

            //System.out.println("replayBuffer.size:"+agent.replayBuffer.size()+", totalNofSteps:"+agent.state.totalNofSteps);

            if (env.isTimeForPolicyTest(iEpisode)) {
                Environment.PolicyTestReturn policyTestReturn = env.testPolicy(agent, env.parameters, env.NOF_TESTS_WHEN_TESTING_POLICY);
                env.printPolicyTest(iEpisode, agent, policyTestReturn, env.parameters.MAX_NOF_STEPS_POLICY_TEST);

                if (policyTestReturn.avgNofSteps>bestNofSteps) {
                    bestNofSteps=policyTestReturn.avgNofSteps;
                    logger.info("New best policy found and saved");
                    agent.network.save(polePolicy);
                    agent.networkTarget.save(polePolicyTarget);
                }
            }
        }

        //plotPolicy();
        TimeUnit.MILLISECONDS.sleep(1000);
        //agent.network.load(polePolicy);
        logger.info("Loading best policy");
        agent.network = ModelSerializer.restoreMultiLayerNetwork(polePolicy);
        Environment.PolicyTestReturn policyTestReturn = env.testPolicy(agent, env.parameters, env.NOF_TESTS_WHEN_TESTING_POLICY);
        env.printPolicyTest(agent.NUM_OF_EPISODES, agent, policyTestReturn, env.parameters.MAX_NOF_STEPS_POLICY_TEST);
        env.animatePolicy(agent, env.parameters);

        System.out.println("nofFits:"+agent.nofFits+", totalNofSteps:"+agent.state.totalNofSteps);
        System.out.println(agent.network.summary());
        Assert.assertTrue(env.testPolicy(agent, env.parameters, env.NOF_TESTS_WHEN_TESTING_POLICY).successRatio>0.8);

    }

    @Test
    public void animateInitPolicy() throws IOException, InterruptedException {
        logger.info("Loading init policy");
        loadPolicy(polePolicyInit, polePolicyInitTarget);
        env.animatePolicy(agent, env.parameters);
    }

    @Test  // @Ignore
    public void animateBestPolicy() throws IOException, InterruptedException {
        logger.info("Loading best policy");
        loadPolicy(polePolicy, polePolicyTarget);
        env.parameters.MAX_NOF_STEPS=(int) 1e6;
        env.animatePolicy(agent, env.parameters);
        System.out.println(agent.state);
        System.out.println("isCartPoleInBadState:"+env.isFailsState(agent.state));
    }

    public void loadPolicy(File polePolicy, File polePolicTarget) throws IOException, InterruptedException {
        agent.network = ModelSerializer.restoreMultiLayerNetwork(polePolicy);
        agent.networkTarget = ModelSerializer.restoreMultiLayerNetwork(polePolicTarget);
        Environment.PolicyTestReturn policyTestReturn = env.testPolicy(agent, env.parameters, env.NOF_TESTS_WHEN_TESTING_POLICY);
        env.printPolicyTest(agent.NUM_OF_EPISODES, agent, policyTestReturn, env.parameters.MAX_NOF_STEPS_POLICY_TEST);
    }


}
