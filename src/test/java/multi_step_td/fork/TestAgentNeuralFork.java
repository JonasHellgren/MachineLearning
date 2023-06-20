package multi_step_td.fork;

import common.RandUtils;
import multi_step_td.TestHelper;
import multi_step_temp_diff.environments.ForkState;
import multi_step_temp_diff.environments.ForkVariables;
import multi_step_temp_diff.interfaces_and_abstract.AgentNeuralInterface;
import multi_step_temp_diff.agents.AgentForkNeural;
import multi_step_temp_diff.environments.ForkEnvironment;
import multi_step_temp_diff.models.NstepExperience;
import multi_step_temp_diff.models.ReplayBufferNStep;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestAgentNeuralFork {

    private static final int BUFFER_SIZE = 100;
    private static final int NOF_ITERATIONS = 1000;
    private static final int BATCH_LENGTH = 30;
    private static final int DELTA = 2;
    AgentNeuralInterface<ForkVariables> agent;
    AgentForkNeural agentCasted;
    TestHelper<ForkVariables> helper;
    ForkEnvironment environment;

    @Before
    public void init () {
        environment = new ForkEnvironment();
        agent= AgentForkNeural.newDefault(environment);
        agentCasted=(AgentForkNeural) agent;
        helper=new TestHelper<>(agent, environment);
    }

    @Test
    public void givenMockedDataAllStatesZero_whenTrain_thenCorrect () {
        final double value = 0d;
        ReplayBufferNStep<ForkVariables> buffer=ReplayBufferNStep.<ForkVariables>builder()
                .buffer(createBatch(value)).build();
        for (int i = 0; i < NOF_ITERATIONS; i++) {
            agent.learn(buffer.getMiniBatch(BATCH_LENGTH));
        }

        helper.printStateValues();
        helper.assertAllStates(value, DELTA);
    }

    @NotNull
    private List<NstepExperience<ForkVariables>> createBatch(double value) {
        List<NstepExperience<ForkVariables>> batch=new ArrayList<>();
        for (int i = 0; i < BUFFER_SIZE; i++) {
            final int randomPos = RandUtils.getRandomIntNumber(0, ForkEnvironment.NOF_STATES);
            NstepExperience<ForkVariables> exp= NstepExperience.<ForkVariables>builder()
                    .stateToUpdate(ForkState.newFromPos(randomPos))
                    .value(value)
                    .build();
            batch.add(exp);
        }
        return batch;
    }


}
