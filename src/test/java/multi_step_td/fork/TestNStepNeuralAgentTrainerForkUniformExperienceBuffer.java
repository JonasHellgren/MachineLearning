package multi_step_td.fork;

import lombok.SneakyThrows;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironmentSettings;
import multi_step_temp_diff.domain.helpers_common.StateValuePrinter;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.ReplayBufferNStepUniform;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.helpers_common.AgentInfo;
import multi_step_temp_diff.domain.agent_parts.neural_memory.normalizer.NormalizeMinMax;
import multi_step_temp_diff.domain.helpers_specific.ForkHelper;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agents.fork.AgentForkNeural;
import multi_step_temp_diff.domain.agent_parts.neural_memory.NetSettings;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.neuroph.util.TransferFunctionType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Big batch seems to destabilize
 */

public class TestNStepNeuralAgentTrainerForkUniformExperienceBuffer {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    private static final int BATCH_SIZE = 10;
    public static final int MAX_SIZE_BUFFER = 10_000;
    private static final int ONE_STEP = 1;
    private static final int NOF_EPIS = 100;
    private static final int START_STATE = 0;
    public static final double LEARNING_RATE = 1e-1;
   // private static final int INPUT_SIZE = ForkEnvironment.envSettings.nofStates();
  //  private static final int NOF_NEURONS_HIDDEN = INPUT_SIZE;

    NStepNeuralAgentTrainer<ForkVariables> trainer;
    AgentNeuralInterface<ForkVariables> agent;
    AgentForkNeural agentCasted;
    ForkEnvironment environment;
    ForkEnvironmentSettings envSettings;

    @BeforeEach
    public void init() {
        environment = new ForkEnvironment();
        envSettings=environment.envSettings;

    }

    @SneakyThrows
    @Test
    @Tag("nettrain")
    public void givenDiscountFactorOne_whenTrained_thenCorrectStateValues() {
        final double discountFactor = 1.0, delta = 2d;
        setAgentAndTrain(discountFactor, NOF_EPIS*2, START_STATE, NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED);
        ForkHelper helper=new ForkHelper(environment);

        printStates();
        AgentInfo<ForkVariables> agentInfo=new AgentInfo<>(agent);
        printBufferSize();

        double avgError = helper.avgErrorFork(agentInfo.stateValueMap(environment.stateSet()));
        assertTrue(avgError < delta);
    }



    @Test
    @Tag("nettrain")
    public void givenDiscountFactorDot9_whenTrained_thenCorrectStateValues() {
        final double discountFactor = 0.9, delta = 3d;
        setAgentAndTrain(discountFactor, NOF_EPIS, START_STATE, NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED);

        printStates();

        final double rHeaven = envSettings.rewardHeaven(),  rHell = envSettings.rewardHell();
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
        printStates();

        final double delta = 1d;
        assertEquals(envSettings.rewardHeaven(),agentCasted.getMemory().read(ForkState.newFromPos(startPos)), delta);
    }


    @Test
    @Tag("nettrain")
    public void givenDiscountFactorDot5SStartAtTwoStepsBeforeHeaven_whenTrained_thenDiscountNotEffectStateValue() {
        final double discountFactor = 0.5;
        final int startPos = 8;
        setAgentAndTrain(discountFactor, BATCH_SIZE*10, startPos, ONE_STEP);

        printBufferSize();

        printStates();
        final double delta = 1d;
        assertEquals(envSettings.rewardHeaven()*discountFactor,
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

        double err3=Math.abs(valueState7ThreeSteps-envSettings.rewardHeaven());
        double err1=Math.abs(valueState7OneStep-envSettings.rewardHeaven());

        assertTrue(err3<err1);

    }


    private void printStates() {
        StateValuePrinter<ForkVariables> stateValuePrinter=new StateValuePrinter<>(agent,environment);
        stateValuePrinter.printStateValues();
    }

    private void setAgentAndTrain(double discountFactor, int nofEpisodes, int startState, int nofStepsBetween) {
        double minOut = envSettings.rewardHell();
        double maxOut = envSettings.rewardHeaven();
        NetSettings netSettings = NetSettings.builder()
                .learningRate(LEARNING_RATE)
                .inputSize(envSettings.nofStates()).nofNeuronsHidden(envSettings.nofStates())
                .transferFunctionType(TransferFunctionType.TANH)
                .minOut(minOut).maxOut(maxOut)
                .normalizer(new NormalizeMinMax(minOut,maxOut)).build();
        agent=AgentForkNeural.newWithDiscountFactorAndMemorySettings(
                environment,
                discountFactor,
                netSettings);

      //  ForkAndMazeHelper<ForkVariables> helper=new ForkAndMazeHelper<>(agent, environment);
        buildTrainer(nofEpisodes, startState, nofStepsBetween);
        trainer.train();
    }

    private void printBufferSize() {
        System.out.println("buffer size = " + trainer.getBuffer().size());
    }

    public void buildTrainer(int nofEpis, int startPos, int nofSteps) {
        agentCasted=(AgentForkNeural) agent;

        NStepNeuralAgentTrainerSettings settings= NStepNeuralAgentTrainerSettings.builder()
                .probStart(0.9).probEnd(1e-5)
                .batchSize(BATCH_SIZE)
                .nofEpis(nofEpis)
                .nofStepsBetweenUpdatedAndBackuped(nofSteps)
                .build();

        trainer= NStepNeuralAgentTrainer.<ForkVariables>builder()
                .settings(settings)
                .startStateSupplier(() -> ForkState.newFromPos(startPos))
                .agentNeural(agent)
                .environment(environment)
                .buffer(ReplayBufferNStepUniform.newFromMaxSize(MAX_SIZE_BUFFER))
                .build();

        System.out.println("buildTrainer");
    }

}
