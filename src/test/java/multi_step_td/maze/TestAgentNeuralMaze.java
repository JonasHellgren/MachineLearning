package multi_step_td.maze;

import common.RandUtils;
import multi_step_td.TestHelper;
import multi_step_temp_diff.agents.AgentForkNeural;
import multi_step_temp_diff.agents.AgentMazeNeural;
import multi_step_temp_diff.environments.*;
import multi_step_temp_diff.interfaces_and_abstract.AgentNeuralInterface;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.NstepExperience;
import multi_step_temp_diff.models.ReplayBufferNStep;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class TestAgentNeuralMaze {


    private static final int BUFFER_SIZE = 100;
    private static final int NOF_ITERATIONS = 3_000;
    private static final int BATCH_LENGTH = 30;
    private static final int DELTA = 2;
    AgentNeuralInterface<MazeVariables> agent;
    AgentMazeNeural agentCasted;
    TestHelper<MazeVariables> helper;
    MazeEnvironment environment;

    Function<StateInterface<MazeVariables>,Double>  sumXy=(s) ->
            (double) MazeState.getX.apply(s)+MazeState.getY.apply(s);

    @Before
    public void init () {
        environment = new MazeEnvironment();
        agent= AgentMazeNeural.newDefault(environment);
        agentCasted=(AgentMazeNeural) agent;
        helper=new TestHelper<>(agentCasted.getMemory(), environment);
    }

    @Test
    public void givenMockedDataAllStatesZero_whenTrain_thenCorrect () {
        ReplayBufferNStep<MazeVariables> buffer=ReplayBufferNStep.<MazeVariables>builder()
                .buffer(createBatch()).build();
        for (int i = 0; i < NOF_ITERATIONS; i++) {
            agent.learn(buffer.getMiniBatch(BATCH_LENGTH));
        }

        helper.printStateValues();
        helper.assertAllStates(sumXy, DELTA);
    }

    @NotNull
    private List<NstepExperience<MazeVariables>> createBatch() {
        List<NstepExperience<MazeVariables>> batch=new ArrayList<>();
        List<StateInterface<MazeVariables>> states=new ArrayList<>(environment.stateSet());
        for (int i = 0; i < BUFFER_SIZE; i++) {
            MazeState state  = (MazeState) states.get(RandUtils.getRandomIntNumber(0,states.size()));
            NstepExperience<MazeVariables> exp= NstepExperience.<MazeVariables>builder()
                    .stateToUpdate(state)
                    .value(sumXy.apply(state))
                    .build();
            batch.add(exp);
        }
        return batch;
    }

}
