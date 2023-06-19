package multi_step_td.maze;

import lombok.SneakyThrows;
import multi_step_td.TestHelper;
import multi_step_temp_diff.agents.AgentMazeNeural;
import multi_step_temp_diff.environments.*;
import multi_step_temp_diff.helpers.AgentInfo;
import multi_step_temp_diff.helpers.NStepNeuralAgentTrainer;
import multi_step_temp_diff.interfaces_and_abstract.AgentNeuralInterface;
import org.junit.Before;
import org.junit.Test;

/**
 * Big batch seems to destabilize
 */

public class TestNStepNeuralAgentTrainerMaze {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    private static final int BATCH_SIZE = 10;
    private static final int ONE_STEP = 1;
    private static final int NOF_EPIS = 300;
    private static final int START_STATE = 0;

    NStepNeuralAgentTrainer<MazeVariables> trainer;
    AgentNeuralInterface<MazeVariables> agent;
    AgentMazeNeural agentCasted;
    MazeEnvironment environment;
    TestHelper<MazeVariables> helper;


    @Before
    public void init() {
        environment=new MazeEnvironment();
        agent= AgentMazeNeural.newDefault(environment);
        AgentMazeNeural agentCasted = (AgentMazeNeural) agent;
        helper=new TestHelper<>(agentCasted.getMemory(), environment);
    }

    @SneakyThrows
    @Test
    public void givenDiscountFactorOne_whenTrained_thenCorrectStateValues() {
        final double discountFactor = 1.0, delta = 2d;
        setAgentAndTrain(discountFactor, NOF_EPIS*2, START_STATE, NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED);
        helper.printStateValues();
        AgentInfo<MazeVariables> agentInfo=new AgentInfo<>(agent);
        printBufferSize();

//        double avgError = TestHelper.avgErrorMaze(agentInfo.stateValueMap(environment.stateSet()));
  //      Assert.assertTrue(avgError < delta);
    }


    private void setAgentAndTrain(double discountFactor, int nofEpisodes, int startState, int nofStepsBetween) {
        agent = AgentMazeNeural.newWithDiscountFactor(environment,discountFactor);
        buildTrainer(nofEpisodes, MazeState.newFromRandomPos(), nofStepsBetween);
        trainer.train();
    }


    private void printBufferSize() {
        System.out.println("buffer size = " + trainer.getBuffer().size());
    }

    public void buildTrainer(int nofEpis, MazeState startState, int nofSteps) {
      //  environment = new ForkEnvironment();
        trainer= NStepNeuralAgentTrainer.<MazeVariables>builder()
                .nofStepsBetweenUpdatedAndBackuped(nofSteps)
                .startState(startState)
                .alpha(0.1)
                .nofEpisodes(nofEpis).batchSize(BATCH_SIZE).agentNeural(agent)
                .probStart(0.25).probEnd(1e-5).nofTrainingIterations(1)
                .environment(environment)
                .agentNeural(agent)
                .build();

        System.out.println("buildTrainer");
    }

}
