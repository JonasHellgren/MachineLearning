package multi_step_td.fork;

import lombok.SneakyThrows;
import multi_step_td.TestHelper;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.helpers.AgentInfo;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agents.fork.AgentForkNeural;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Big batch seems to destabilize
 */

public class TestNStepNeuralAgentTrainerFork {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    private static final int BATCH_SIZE = 10;
    private static final int ONE_STEP = 1;
    private static final int NOF_EPIS = 100;
    private static final int START_STATE = 0;
    public static final double LEARNING_RATE = 1e-1;
    private static final int INPUT_SIZE = ForkEnvironment.NOF_STATES;
    private static final int NOF_NEURONS_HIDDEN = INPUT_SIZE;

    NStepNeuralAgentTrainer<ForkVariables> trainer;
    AgentNeuralInterface<ForkVariables> agent;
    AgentForkNeural agentCasted;
    ForkEnvironment environment;
    TestHelper<ForkVariables> helper;


    @BeforeEach
    public void init() {
        environment = new ForkEnvironment();
    }

    @SneakyThrows
    @Test
    @Tag("nettrain")
    public void givenDiscountFactorOne_whenTrained_thenCorrectStateValues() {
        final double discountFactor = 1.0, delta = 2d;
        setAgentAndTrain(discountFactor, NOF_EPIS*2, START_STATE, NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED);
        helper.printStateValues();
        AgentInfo<ForkVariables> agentInfo=new AgentInfo<>(agent);
        printBufferSize();

        double avgError = TestHelper.avgErrorFork(agentInfo.stateValueMap(environment.stateSet()));
        assertTrue(avgError < delta);
    }


    @Test
    @Tag("nettrain")
    public void givenDiscountFactorDot9_whenTrained_thenCorrectStateValues() {
        final double discountFactor = 0.9, delta = 3d;
        setAgentAndTrain(discountFactor, NOF_EPIS, START_STATE, NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED);

        helper.printStateValues();

        final double rHeaven = ForkEnvironment.R_HEAVEN, rHell = ForkEnvironment.R_HELL;
        assertEquals(Math.pow(discountFactor,10-7)* rHeaven,agentCasted.getMemory().read(ForkState.newFromPos(7)), delta);
        assertEquals(Math.pow(discountFactor,10-9)*rHeaven,agentCasted.getMemory().read(ForkState.newFromPos(9)), delta);
        assertEquals(Math.pow(discountFactor,9)*rHeaven,agentCasted.getMemory().read(ForkState.newFromPos(0)), delta);

        assertEquals(Math.pow(discountFactor,15-13)*rHell,agentCasted.getMemory().read(ForkState.newFromPos(13)), delta);
        assertEquals(Math.pow(discountFactor,5)*rHell,agentCasted.getMemory().read(ForkState.newFromPos(6)), delta);
    }

    @Test
    @Tag("nettrain")
    public void givenDiscountFactorDot5SStartAtBeforeHeaven_whenTrained_thenDiscountNotEffectStateValue() {
        final double discountFactor = 0.5;
        final int nofEpisodes=BATCH_SIZE*10, startPos = 9;
        setAgentAndTrain(discountFactor, nofEpisodes, startPos, ONE_STEP);
        printBufferSize();
        helper.printStateValues();
        final double delta = 1d;
        assertEquals(ForkEnvironment.R_HEAVEN,agentCasted.getMemory().read(ForkState.newFromPos(startPos)), delta);
    }

    private void setAgentAndTrain(double discountFactor, int nofEpisodes, int startState, int nofStepsBetween) {
        NetSettings netSettings = NetSettings.builder()
                .learningRate(LEARNING_RATE)
                .inputSize(INPUT_SIZE).nofNeuronsHidden(NOF_NEURONS_HIDDEN)
                .minOut(ForkEnvironment.R_HELL).maxOut(ForkEnvironment.R_HEAVEN).build();
        agent=AgentForkNeural.newWithDiscountFactorAndMemorySettings(
                environment,
                discountFactor,
                netSettings);
        helper=new TestHelper<>(agent, environment);
        buildTrainer(nofEpisodes, startState, nofStepsBetween);
        trainer.train();
    }

    @Test
    @Tag("nettrain")
    public void givenDiscountFactorDot5SStartAtTwoStepsBeforeHeaven_whenTrained_thenDiscountNotEffectStateValue() {
        final double discountFactor = 0.5;
        final int startPos = 8;
        setAgentAndTrain(discountFactor, BATCH_SIZE*10, startPos, ONE_STEP);

        printBufferSize();
        helper.printStateValues();
        final double delta = 1d;
        assertEquals(ForkEnvironment.R_HEAVEN*discountFactor,
                agentCasted.getMemory().read(ForkState.newFromPos(startPos)), delta);
    }

    @Test
    @Tag("nettrain")
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

        assertTrue(err3<err1);

    }

    private void printBufferSize() {
        System.out.println("buffer size = " + trainer.getBuffer().size());
    }

    public void buildTrainer(int nofEpis, int startPos, int nofSteps) {
        agentCasted=(AgentForkNeural) agent;
      //  environment = new ForkEnvironment();
        trainer= NStepNeuralAgentTrainer.<ForkVariables>builder()
                .nofStepsBetweenUpdatedAndBackuped(nofSteps)
                .startStateSupplier(() -> ForkState.newFromPos(startPos))
                //.alpha(0.1)
                .nofEpisodes(nofEpis).batchSize(BATCH_SIZE).agentNeural(agent)
                .probStart(0.9).probEnd(1e-5).nofTrainingIterations(1)
                .environment(environment)
                .agentNeural(agent)
                .build();

        System.out.println("buildTrainer");
    }

}
