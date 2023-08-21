package multi_step_td.fork;

import lombok.SneakyThrows;
import multi_step_temp_diff.domain.environment_valueobj.ForkEnvironmentSettings;
import multi_step_temp_diff.domain.helpers_specific.ForkAndMazeHelper;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.normalizer.NormalizerMeanStd;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestNStepNeuralAgentTrainerForkPrioritizedExperienceBuffer {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    private static final int BATCH_SIZE = 30, NOF_EPIS = 100;
    public static final int MAX_SIZE_BUFFER = 10_000;

    private static final int START_STATE = 0;
    public static final double LEARNING_RATE = 1e-1;
   // private static final int INPUT_SIZE = ForkEnvironment.envSettings.nofStates(), NOF_NEURONS_HIDDEN = INPUT_SIZE;
    public static final double DISCOUNT_FACTOR = 1.0;
    public static final int NOF_EXPERIENCE_ADDING_BETWEEN_PROBABILITY_SETTING = 10;

    NStepNeuralAgentTrainer<ForkVariables> trainer;
    AgentNeuralInterface<ForkVariables> agent;
    AgentForkNeural agentCasted;
    ForkEnvironment environment;
    ForkAndMazeHelper<ForkVariables> helper;
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
        double delta = 2d;
        buildAgent();
        buildTrainer();
        trainer.train();

        helper=new ForkAndMazeHelper<>(agent, environment);
        helper.printStateValues();
        AgentInfo<ForkVariables> agentInfo=new AgentInfo<>(agent);

        System.out.println("trainer.getBuffer().size() = " + trainer.getBuffer().size());

        ForkAndMazeHelper<ForkVariables> helper=new ForkAndMazeHelper<>(agent,environment);
        double avgError = helper.avgErrorFork(agentInfo.stateValueMap(environment.stateSet()));
        assertTrue(avgError < delta);
    }

    private void buildAgent() {
        double minOut = envSettings.rewardHell();
        double maxOut = envSettings.rewardHeaven();
        NetSettings netSettings = NetSettings.builder()
                .learningRate(LEARNING_RATE)
                .inputSize(envSettings.nofStates()).nofNeuronsHidden(envSettings.nofStates())
                .transferFunctionType(TransferFunctionType.TANH)
                .minOut(minOut).maxOut(maxOut)
                .normalizer(new NormalizerMeanStd(List.of(10*minOut,10*maxOut,0d,0d,0d))).build();
        //        .normalizer(new NormalizeMinMax(minOut*2,maxOut*2)).build();  //also works

        agent=AgentForkNeural.newWithDiscountFactorAndMemorySettings(
                environment,
                DISCOUNT_FACTOR,
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
                .buffer(ReplayBufferNStepPrioritized.<ForkVariables>builder()
                        .maxSize(MAX_SIZE_BUFFER)
                        .nofExperienceAddingBetweenProbabilitySetting(NOF_EXPERIENCE_ADDING_BETWEEN_PROBABILITY_SETTING)
                        .build())
                .build();
    }

}
