package multi_step_td.maze;

import lombok.SneakyThrows;
import multi_step_td.TestHelper;
import multi_step_temp_diff.environments.ForkState;
import multi_step_temp_diff.environments.ForkVariables;
import multi_step_temp_diff.helpers.AgentInfo;
import multi_step_temp_diff.helpers.NStepNeuralAgentTrainer;
import multi_step_temp_diff.interfaces_and_abstract.AgentNeuralInterface;
import multi_step_temp_diff.agents.AgentForkNeural;
import multi_step_temp_diff.environments.ForkEnvironment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Big batch seems to destabilize
 */

public class TestNStepNeuralAgentTrainerMaze {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    private static final int BATCH_SIZE = 10;
    private static final int ONE_STEP = 1;
    private static final int NOF_EPIS = 300;
    private static final int START_STATE = 0;

    NStepNeuralAgentTrainer<ForkVariables> trainer;
    AgentNeuralInterface<ForkVariables> agent;
    AgentForkNeural agentCasted;
    ForkEnvironment environment;
    TestHelper<ForkVariables> helper;


    @Before
    public void init() {
        environment=new ForkEnvironment();
        agent= AgentForkNeural.newDefault(environment);
        AgentForkNeural agentCasted=(AgentForkNeural) agent;
        helper=new TestHelper<>(agentCasted.getMemory(), environment);
    }

    @SneakyThrows
    @Test
    public void givenDiscountFactorOne_whenTrained_thenCorrectStateValues() {
        final double discountFactor = 1.0, delta = 2d;
        setAgentAndTrain(discountFactor, NOF_EPIS*2, START_STATE, NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED);
        helper.printStateValues();
        AgentInfo<ForkVariables> agentInfo=new AgentInfo<>(agent);
        printBufferSize();

        double avgError = TestHelper.avgErrorFork(agentInfo.stateValueMap(environment.stateSet()));
        Assert.assertTrue(avgError < delta);
    }


    @Test
    public void givenDiscountFactorDot9_whenTrained_thenCorrectStateValues() {
        final double discountFactor = 0.9, delta = 3d;
        setAgentAndTrain(discountFactor, NOF_EPIS, START_STATE, NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED);

        helper.printStateValues();

        final double rHeaven = ForkEnvironment.R_HEAVEN, rHell = ForkEnvironment.R_HELL;
        Assert.assertEquals(Math.pow(discountFactor,10-7)* rHeaven,agentCasted.getMemory().read(ForkState.newFromPos(7)), delta);
        Assert.assertEquals(Math.pow(discountFactor,10-9)*rHeaven,agentCasted.getMemory().read(ForkState.newFromPos(9)), delta);
        Assert.assertEquals(Math.pow(discountFactor,9)*rHeaven,agentCasted.getMemory().read(ForkState.newFromPos(0)), delta);

        Assert.assertEquals(Math.pow(discountFactor,15-13)*rHell,agentCasted.getMemory().read(ForkState.newFromPos(13)), delta);
        Assert.assertEquals(Math.pow(discountFactor,5)*rHell,agentCasted.getMemory().read(ForkState.newFromPos(6)), delta);
    }

    @Test
    public void givenDiscountFactorDot5SStartAtBeforeHeaven_whenTrained_thenDiscountNotEffectStateValue() {
        final double discountFactor = 0.5;
        final int nofEpisodes=BATCH_SIZE*10, startPos = 9;
        setAgentAndTrain(discountFactor, nofEpisodes, startPos, ONE_STEP);
        printBufferSize();
        helper.printStateValues();
        final double delta = 1d;
        Assert.assertEquals(ForkEnvironment.R_HEAVEN,agentCasted.getMemory().read(ForkState.newFromPos(startPos)), delta);
    }

    private void setAgentAndTrain(double discountFactor, int nofEpisodes, int startState, int nofStepsBetween) {
        agent = AgentForkNeural.newWithDiscountFactor(environment,discountFactor);
        buildTrainer(nofEpisodes, startState, nofStepsBetween);
        trainer.train();
    }

    @Test
    public void givenDiscountFactorDot5SStartAtTwoStepsBeforeHeaven_whenTrained_thenDiscountNotEffectStateValue() {
        final double discountFactor = 0.5;
        final int startPos = 8;
        setAgentAndTrain(discountFactor, BATCH_SIZE*10, startPos, ONE_STEP);

        printBufferSize();
        helper.printStateValues();
        final double delta = 1d;
        Assert.assertEquals(ForkEnvironment.R_HEAVEN*discountFactor,
                agentCasted.getMemory().read(ForkState.newFromPos(startPos)), delta);
    }

    @Test
    public void givenStartingAtState7_whenTrainedWithMoreSteps_thenFasterLearning() {
        final double discountFactor = 1.00;
        final int startPos = 7, nofEpis = BATCH_SIZE * 2;
        setAgentAndTrain(discountFactor, nofEpis, startPos, ONE_STEP);
        double valueState7OneStep = agentCasted.getMemory().read(ForkState.newFromPos(startPos));

        setAgentAndTrain(discountFactor, nofEpis, startPos, NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED);
        double valueState7ThreeSteps = agentCasted.getMemory().read(ForkState.newFromPos(startPos));

        System.out.println("valueState7OneStep = " + valueState7OneStep + ", valueState7ThreeSteps = " + valueState7ThreeSteps);

        double err3=Math.abs(valueState7ThreeSteps-ForkEnvironment.R_HEAVEN);
        double err1=Math.abs(valueState7OneStep-ForkEnvironment.R_HEAVEN);

        Assert.assertTrue(err3<err1);


    }

    private void printBufferSize() {
        System.out.println("buffer size = " + trainer.getBuffer().size());
    }

    public void buildTrainer(int nofEpis, int startPos, int nofSteps) {
        agentCasted=(AgentForkNeural) agent;
      //  environment = new ForkEnvironment();
        trainer= NStepNeuralAgentTrainer.<ForkVariables>builder()
                .nofStepsBetweenUpdatedAndBackuped(nofSteps)
                .startState(ForkState.newFromPos(startPos))
                .alpha(0.1)
                .nofEpisodes(nofEpis).batchSize(BATCH_SIZE).agentNeural(agent)
                .probStart(0.25).probEnd(1e-5).nofTrainingIterations(1)
                .environment(environment)
                .agentNeural(agent)
                .build();

        System.out.println("buildTrainer");
    }

}
