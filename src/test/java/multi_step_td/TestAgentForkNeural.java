package multi_step_td;

import common.RandUtils;
import multi_step_temp_diff.interfaces.AgentNeuralInterface;
import multi_step_temp_diff.models.AgentForkNeural;
import multi_step_temp_diff.models.ForkEnvironment;
import multi_step_temp_diff.models.NstepExperience;
import multi_step_temp_diff.models.ReplayBufferNStep;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestAgentForkNeural {

    private static final int BUFFER_SIZE = 100;
    private static final int NOF_ITERATIONS = 1000;
    private static final int BATCH_LENGTH = 30;
    private static final int DELTA = 2;
    AgentNeuralInterface agent;
    AgentForkNeural agentCasted;

    @Before
    public void init () {
        agent= AgentForkNeural.newDefault();
        agentCasted=(AgentForkNeural) agent;
    }

    @Test
    public void givenMockedDataAllStatesZero_whenTrain_thenCorrect () {
        final double value = 0d;
        ReplayBufferNStep buffer=ReplayBufferNStep.builder()
                .buffer(createBatch(value)).build();
        for (int i = 0; i < NOF_ITERATIONS; i++) {
            agent.learn(buffer.getMiniBatch(BATCH_LENGTH));
        }

        TestHelper.printStateValues(agentCasted.getMemory());
        TestHelper.assertAllStates(agentCasted.getMemory(),value, DELTA);
    }

    @NotNull
    private List<NstepExperience> createBatch(double value) {
        List<NstepExperience> batch=new ArrayList<>();
        for (int i = 0; i < BUFFER_SIZE; i++) {
            NstepExperience exp= NstepExperience.builder()
                    .stateToUpdate(RandUtils.getRandomIntNumber(0, ForkEnvironment.NOF_STATES))
                    .value(value)
                    .build();
            batch.add(exp);
        }
        return batch;
    }


}
