package multi_step_td.fork;

import common.RandUtils;
import multi_step_td.helpers.StateAsserter;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironmentSettings;
import multi_step_temp_diff.domain.helpers_common.StateValuePrinter;
import multi_step_temp_diff.domain.helpers_specific.MazeHelper;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agents.fork.AgentForkNeural;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.ReplayBufferNStepUniform;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


public class TestAgentNeuralFork {

    private static final int BUFFER_SIZE = 100;
    private static final int NOF_ITERATIONS = 1000;
    private static final int BATCH_LENGTH = 30;
    private static final int DELTA = 2;
    AgentNeuralInterface<ForkVariables> agent;
    AgentForkNeural agentCasted;
    MazeHelper<ForkVariables> helper;
    ForkEnvironment environment;
    ForkEnvironmentSettings envSettings;

    @BeforeEach
    public void init () {
        environment = new ForkEnvironment();
        agent= AgentForkNeural.newDefault(environment);
        agentCasted=(AgentForkNeural) agent;
        helper=new MazeHelper<>(agent, environment);
        envSettings=environment.envSettings;
    }

    @Test
    @Tag("nettrain")
    public void givenMockedDataAllStatesZero_whenTrain_thenCorrect () {
        final double value = 0d;
        ReplayBufferNStepUniform<ForkVariables> buffer= ReplayBufferNStepUniform.<ForkVariables>builder()
                .buffer(createBatch(value)).build();
        for (int i = 0; i < NOF_ITERATIONS; i++) {
            agent.learn(buffer.getMiniBatch(BATCH_LENGTH));
        }

       printStates();
        StateAsserter  stateAsserter=new StateAsserter<>(agent,environment);
        stateAsserter.assertAllStates(value, DELTA);
    }

    @NotNull
    private List<NstepExperience<ForkVariables>> createBatch(double value) {
        List<NstepExperience<ForkVariables>> batch=new ArrayList<>();
        for (int i = 0; i < BUFFER_SIZE; i++) {
            final int randomPos = RandUtils.getRandomIntNumber(0, envSettings.nofStates());
            NstepExperience<ForkVariables> exp= NstepExperience.<ForkVariables>builder()
                    .stateToUpdate(ForkState.newFromPos(randomPos))
                    .value(value)
                    .build();
            batch.add(exp);
        }
        return batch;
    }

    private void printStates() {
        StateValuePrinter<ForkVariables> stateValuePrinter=new StateValuePrinter<>(agent,environment);
        stateValuePrinter.printStateValues();
    }

}
