package multi_step_td.fork;

import lombok.SneakyThrows;
import multi_step_td.TestHelper;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.normalizer.NormalizeMinMax;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStepPrioritized;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.agents.fork.AgentForkNeural;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.helpers_common.AgentInfo;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.neuroph.util.TransferFunctionType;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestNStepNeuralAgentTrainerForkPrioritizedExperienceBuffer {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    private static final int BATCH_SIZE = 10;
    private static final int ONE_STEP = 1;
    private static final int NOF_EPIS = 100;
    private static final int START_STATE = 0;
    public static final double LEARNING_RATE = 1e-1;
    private static final int INPUT_SIZE = ForkEnvironment.envSettings.nofStates();
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
        buildAgent(discountFactor);
        buildTrainer();
        trainer.train();

        helper=new TestHelper<>(agent, environment);
        helper.printStateValues();
        AgentInfo<ForkVariables> agentInfo=new AgentInfo<>(agent);

        System.out.println("trainer.getBuffer().size() = " + trainer.getBuffer().size());

        double avgError = TestHelper.avgErrorFork(agentInfo.stateValueMap(environment.stateSet()));
        assertTrue(avgError < delta);
    }

    private void buildAgent(double discountFactor) {
        double minOut = ForkEnvironment.envSettings.rewardHell();
        double maxOut = ForkEnvironment.envSettings.rewardHeaven();
        NetSettings netSettings = NetSettings.builder()
                .learningRate(LEARNING_RATE)
                .inputSize(INPUT_SIZE).nofNeuronsHidden(NOF_NEURONS_HIDDEN)
                .transferFunctionType(TransferFunctionType.TANH)
                .minOut(minOut).maxOut(maxOut)
                .normalizer(new NormalizeMinMax(minOut,maxOut)).build();
        agent=AgentForkNeural.newWithDiscountFactorAndMemorySettings(
                environment,
                discountFactor,
                netSettings);
    }

    public void buildTrainer() {
        agentCasted=(AgentForkNeural) agent;

        NStepNeuralAgentTrainerSettings settings= NStepNeuralAgentTrainerSettings.builder()
                .probStart(0.9).probEnd(1e-5).nofIterations(1)
                .batchSize(BATCH_SIZE)
                .nofEpis(NOF_EPIS)
                .nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .build();

        trainer= NStepNeuralAgentTrainer.<ForkVariables>builder()
                .settings(settings)
                .startStateSupplier(() -> ForkState.newFromPos(START_STATE))
                .agentNeural(agent)
                .environment(environment)
                .buffer(ReplayBufferNStepPrioritized.newDefault())
                .build();
    }

}
