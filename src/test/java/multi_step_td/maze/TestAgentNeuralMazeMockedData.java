package multi_step_td.maze;

import common.RandUtils;
import multi_step_td.TestHelper;
import multi_step_temp_diff.domain.agent_valueobj.AgentMazeNeuralSettings;
import multi_step_temp_diff.domain.agents.maze.AgentMazeNeural;
import multi_step_temp_diff.domain.environments.maze.MazeEnvironment;
import multi_step_temp_diff.domain.environments.maze.MazeState;
import multi_step_temp_diff.domain.environments.maze.MazeVariables;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStep;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TestAgentNeuralMazeMockedData {
    private static final int NOF_ITERATIONS = 1000;
    private static final int BUFFER_SIZE = NOF_ITERATIONS;

    private static final int BATCH_LENGTH = 100;
    private static final int DELTA = 2;
    public static final double LEARNING_RATE = 5e-1;
    AgentNeuralInterface<MazeVariables> agent;
    AgentMazeNeural agentCasted;
    TestHelper<MazeVariables> helper;
    MazeEnvironment environment;

    Function<StateInterface<MazeVariables>,Double>  sumXy=(s) ->
            (double) 80+ MazeState.getX.apply(s)+MazeState.getY.apply(s);

    @BeforeEach
    public void init () {
        environment = new MazeEnvironment();
        AgentMazeNeuralSettings agentSettings=AgentMazeNeuralSettings.builder()
                .discountFactor(1).learningRate(LEARNING_RATE)
                .build();
        agent=new AgentMazeNeural(environment,agentSettings);

        agentCasted=(AgentMazeNeural) agent;
        helper=new TestHelper<>(agent, environment);
    }

    @Test
    @Tag("nettrain")
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
