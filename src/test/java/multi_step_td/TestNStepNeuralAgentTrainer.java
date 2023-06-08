package multi_step_td;

import multi_step_temp_diff.helpers.AgentInfo;
import multi_step_temp_diff.helpers.NStepNeuralAgentTrainer;
import multi_step_temp_diff.interfaces.AgentNeuralInterface;
import multi_step_temp_diff.models.AgentForkNeural;
import multi_step_temp_diff.models.ForkEnvironment;
import org.junit.Assert;
import org.junit.Test;

/**
 * Big batch seems to destabilize
 */

public class TestNStepNeuralAgentTrainer {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    private static final int BATCH_SIZE = 10;
    private static final int ONE_STEP = 1;
    private static final int NOF_EPIS = 300;
    private static final int START_STATE = 0;
    NStepNeuralAgentTrainer trainer;
    AgentNeuralInterface agent;
    AgentForkNeural agentCasted;
    ForkEnvironment environment;


    @Test
    public void givenDiscountFactorOne_whenTrained_thenCorrectStateValues() {
        final double discountFactor = 1.0, delta = 2d;
        setAgentAndTrain(discountFactor, NOF_EPIS, START_STATE, NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED);
        TestHelper.printStateValues(agentCasted.getMemory());
        AgentInfo agentInfo=new AgentInfo(agent);
        printBufferSize();
        Assert.assertTrue(TestHelper.avgError(agentInfo.stateValueMap(environment.stateSet())) < delta);
    }


    @Test
    public void givenDiscountFactorDot9_whenTrained_thenCorrectStateValues() {
        final double discountFactor = 0.9, delta = 3d;
        setAgentAndTrain(discountFactor, NOF_EPIS, START_STATE, NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED);

        TestHelper.printStateValues(agentCasted.getMemory());

        final double rHeaven = ForkEnvironment.R_HEAVEN, rHell = ForkEnvironment.R_HELL;
        Assert.assertEquals(Math.pow(discountFactor,10-7)* rHeaven,agentCasted.getMemory().read(7), delta);
        Assert.assertEquals(Math.pow(discountFactor,10-9)*rHeaven,agentCasted.getMemory().read(9), delta);
        Assert.assertEquals(Math.pow(discountFactor,9)*rHeaven,agentCasted.getMemory().read(0), delta);

        Assert.assertEquals(Math.pow(discountFactor,15-13)*rHell,agentCasted.getMemory().read(13), delta);
        Assert.assertEquals(Math.pow(discountFactor,5)*rHell,agentCasted.getMemory().read(6), delta);
    }

    @Test
    public void givenDiscountFactorDot5SStartAtBeforeHeaven_whenTrained_thenDiscountNotEffectStateValue() {
        final double discountFactor = 0.5;
        final int nofEpisodes=BATCH_SIZE*10, startState = 9;
        setAgentAndTrain(discountFactor, nofEpisodes, startState, ONE_STEP);
        printBufferSize();
        TestHelper.printStateValues(agentCasted.getMemory());
        final double delta = 1d;
        Assert.assertEquals(ForkEnvironment.R_HEAVEN,agentCasted.getMemory().read(startState), delta);
    }

    private void setAgentAndTrain(double discountFactor, int nofEpisodes, int startState, int nofStepsBetween) {
        agent = AgentForkNeural.newWithDiscountFactor(discountFactor);
        buildTrainer(nofEpisodes, startState, nofStepsBetween);
        trainer.train();
    }

    @Test
    public void givenDiscountFactorDot5SStartAtTwoStepsBeforeHeaven_whenTrained_thenDiscountNotEffectStateValue() {
        final double discountFactor = 0.5;
        final int startState = 8;
        setAgentAndTrain(discountFactor, BATCH_SIZE*10, startState, ONE_STEP);

        printBufferSize();
        TestHelper.printStateValues(agentCasted.getMemory());
        final double delta = 1d;
        Assert.assertEquals(ForkEnvironment.R_HEAVEN*discountFactor,agentCasted.getMemory().read(startState), delta);
    }

    @Test public void givenStartingAtState7_whenTrainedWithMoreSteps_thenFasterLearning() {
        final double discountFactor = 1.00;
        final int startState = 7, nofEpis = BATCH_SIZE * 2;
        setAgentAndTrain(discountFactor, nofEpis, startState, ONE_STEP);
        double valueState7OneStep = agentCasted.getMemory().read(startState);

        setAgentAndTrain(discountFactor, nofEpis, startState, NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED);
        double valueState7ThreeSteps = agentCasted.getMemory().read(startState);

        System.out.println("valueState7OneStep = " + valueState7OneStep + ", valueState7ThreeSteps = " + valueState7ThreeSteps);

        double err3=Math.abs(valueState7ThreeSteps-ForkEnvironment.R_HEAVEN);
        double err1=Math.abs(valueState7OneStep-ForkEnvironment.R_HEAVEN);

        Assert.assertTrue(err3<err1);


    }

    private void printBufferSize() {
        System.out.println("buffer size = " + trainer.getBuffer().size());
    }

    public void buildTrainer(int nofEpis, int startState, int nofSteps) {
        agentCasted=(AgentForkNeural) agent;
        environment = new ForkEnvironment();
        trainer= NStepNeuralAgentTrainer.builder()
                .nofStepsBetweenUpdatedAndBackuped(nofSteps)
                .startState(startState)
                .nofEpisodes(nofEpis).batchSize(BATCH_SIZE).agentNeural(agent)
                .probStart(0.5).probEnd(0.01).nofTrainingIterations(1)
                .environment(environment)
                .agentNeural(agent)
                .build();
    }

}
