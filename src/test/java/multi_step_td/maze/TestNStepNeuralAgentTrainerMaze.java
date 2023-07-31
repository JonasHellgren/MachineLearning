package multi_step_td.maze;

import lombok.SneakyThrows;
import multi_step_td.TestHelper;
import multi_step_temp_diff.domain.agents.maze.AgentMazeNeural;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.environments.maze.MazeEnvironment;
import multi_step_temp_diff.domain.environments.maze.MazeState;
import multi_step_temp_diff.domain.environments.maze.MazeVariables;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import multi_step_temp_diff.domain.helpers.StateValuePrinter;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Big batch seems to destabilize
 */

public class TestNStepNeuralAgentTrainerMaze {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 3;
    private static final int BATCH_SIZE = 50, BUFFER_SIZE_MAX = 100;
    private static final int NOF_EPIS = 500;
    public static final List<MazeState> STATES_LIST = TestHelper.STATES_MAZE_UPPER;
    public static final HashSet<StateInterface<MazeVariables>> STATE_SET = new HashSet<>(STATES_LIST);
    public static final double LEARNING_RATE =1e-1;
    public static final double PROB_START = 0.1, PROB_END = 1e-5;

    NStepNeuralAgentTrainer<MazeVariables> trainer;
    AgentNeuralInterface<MazeVariables> agent;
    AgentMazeNeural agentCasted;
    MazeEnvironment environment;
    TestHelper<MazeVariables> helper;

    @BeforeEach
    public void init() {
        environment=new MazeEnvironment();
        agent= AgentMazeNeural.newDefault(environment);
        agentCasted = (AgentMazeNeural) agent;
        helper=new TestHelper<>(agent, environment);
    }

    BiFunction<Integer,Integer,Double> value=(x,y) ->  agent.readValue(MazeState.newFromXY(x,y));

    @SneakyThrows
    @Test
    @Tag("nettrain")
    public void givenDiscountFactorOne_whenTrained_thenCorrectStateValues() {
        final double discountFactor = 1.0, delta = 10d;
        setAgentAndTrain(discountFactor,LEARNING_RATE);
        printBufferSize();

        helper=new TestHelper<>(agent, environment);
        helper.printStateValues(new HashSet<>(TestHelper.STATES_MAZE_UPPER));
        helper.printStateValues(new HashSet<>(TestHelper.STATES_MAZE_MIDDLE));
        double avgError=TestHelper.avgErrorMaze(helper.getStateValueMap(STATE_SET),STATES_LIST);

        System.out.println("avgError = " + avgError);

        StateValuePrinter<MazeVariables> printer=new StateValuePrinter<>(agent,environment);
        printer.printMazeNeuralAgent();

        assertTrue(avgError < delta);
        assertTrue(value.apply(3,5)>value.apply(2,5));
        assertTrue(value.apply(2,5)>value.apply(1,5));
        assertTrue(value.apply(1,5)>value.apply(0,5));


    }


    private void setAgentAndTrain(double discountFactor,double learningRate) {
        agent = AgentMazeNeural.newWithDiscountFactorAndLearningRate(environment,discountFactor,learningRate);

        var settings= NStepNeuralAgentTrainerSettings.builder()
                .probStart(PROB_START).probEnd(PROB_END).nofIterations(1)
                .batchSize(BATCH_SIZE)
                .nofEpis(NOF_EPIS).batchSize(BATCH_SIZE).maxBufferSize(BUFFER_SIZE_MAX)
                .nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .build();

        trainer= NStepNeuralAgentTrainer.<MazeVariables>builder()
                .settings(settings)
                .startStateSupplier(MazeState::newFromRandomPos)
                .agentNeural(agent)
                .environment(environment)
                .build();

        trainer.train();

    }


    private void printBufferSize() {
        System.out.println("buffer size = " + trainer.getBuffer().size());
    }


}
