package multi_step_td.maze;

import common.ListUtils;
import lombok.SneakyThrows;
import multi_step_td.TestHelper;
import multi_step_temp_diff.agents.AgentMazeNeural;
import multi_step_temp_diff.environments.*;
import multi_step_temp_diff.helpers.AgentInfo;
import multi_step_temp_diff.helpers.NStepNeuralAgentTrainer;
import multi_step_temp_diff.interfaces_and_abstract.AgentNeuralInterface;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Big batch seems to destabilize
 */

public class TestNStepNeuralAgentTrainerMaze {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 3;
    private static final int BATCH_SIZE = 10, BUFFER_SIZE_MAX = 100;
    private static final int NOF_EPIS = 100;
    public static final List<MazeState> STATES_LIST = TestHelper.STATES_MAZE_UPPER;
    public static final HashSet<StateInterface<MazeVariables>> STATE_SET = new HashSet<>(STATES_LIST);
    public static final double LEARNING_RATE =1e-1;
    public static final double PROB_START = 0.2, PROB_END = 1e-5;

    NStepNeuralAgentTrainer<MazeVariables> trainer;
    AgentNeuralInterface<MazeVariables> agent;
    AgentMazeNeural agentCasted;
    MazeEnvironment environment;
    TestHelper<MazeVariables> helper;

    @Before
    public void init() {
        environment=new MazeEnvironment();
        agent= AgentMazeNeural.newDefault(environment);
        agentCasted = (AgentMazeNeural) agent;
        helper=new TestHelper<>(agent, environment);
    }

    BiFunction<Integer,Integer,Double> value=(x,y) ->  agent.readValue(MazeState.newFromXY(x,y));

    @SneakyThrows
    @Test
    public void givenDiscountFactorOne_whenTrained_thenCorrectStateValues() {
        final double discountFactor = 1.0, delta = 10d;
        setAgentAndTrain(discountFactor,LEARNING_RATE);
     //   helper.printStateValues();
       // AgentInfo<MazeVariables> agentInfo=new AgentInfo<>(agent);
        printBufferSize();

       System.out.println("trainer.getBuffer() = " + trainer.getBuffer());

        helper=new TestHelper<>(agent, environment);
        helper.printStateValues(new HashSet<>(TestHelper.STATES_MAZE_UPPER));
        helper.printStateValues(new HashSet<>(TestHelper.STATES_MAZE_NEXT_UPPER));


        double avgError=TestHelper.avgErrorMaze(helper.getStateValueMap(STATE_SET),STATES_LIST);

        System.out.println("avgError = " + avgError);

//        double avgError = TestHelper.avgErrorMaze(agentInfo.stateValueMap(environment.stateSet()));
        Assert.assertTrue(avgError < delta);
        Assert.assertTrue(value.apply(3,5)>value.apply(2,5));
        Assert.assertTrue(value.apply(2,5)>value.apply(1,5));
        Assert.assertTrue(value.apply(1,5)>value.apply(0,5));


    }


    private void setAgentAndTrain(double discountFactor,double learningRate) {
        agent = AgentMazeNeural.newWithDiscountFactorAndLearningRate(environment,discountFactor,learningRate);
        trainer= NStepNeuralAgentTrainer.<MazeVariables>builder()
                .nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .startStateSupplier(MazeState::newFromRandomPos)

                //.startStateSupplier(() -> MazeState.newFromXY(2,5))
                //.alpha(ALPHA)
                .nofEpisodes(NOF_EPIS).batchSize(BATCH_SIZE).bufferSizeMax(BUFFER_SIZE_MAX)
                .agentNeural(agent)
                .probStart(PROB_START).probEnd(PROB_END).nofTrainingIterations(1)
                .environment(environment)
                .maxStepsInEpisode(10)
                .agentNeural(agent)
                .build();
        trainer.train();
    }


    private void printBufferSize() {
        System.out.println("buffer size = " + trainer.getBuffer().size());
    }


}
