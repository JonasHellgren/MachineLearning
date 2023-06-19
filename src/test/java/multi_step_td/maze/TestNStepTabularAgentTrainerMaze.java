package multi_step_td.maze;

import    lombok.SneakyThrows;
import multi_step_td.TestHelper;
import multi_step_temp_diff.agents.AgentMazeTabular;
import multi_step_temp_diff.environments.*;
import multi_step_temp_diff.helpers.NStepTabularAgentTrainer;
import multi_step_temp_diff.interfaces_and_abstract.EnvironmentInterface;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class TestNStepTabularAgentTrainerMaze {

    private static final int ONE_STEP = 1;
    private static final int THREE_STEPS = 3;
    private static final int NOF_EPISODES = 100;
    NStepTabularAgentTrainer<MazeVariables> trainer;
    AgentMazeTabular agent;


    @Before
    public void init() {
        final MazeEnvironment environment = new MazeEnvironment();
        agent = AgentMazeTabular.newDefault(environment);
        trainer= NStepTabularAgentTrainer.<MazeVariables>builder()
                .nofEpisodes(NOF_EPISODES)
                .alpha(0.2).probStart(0.25).probEnd(1e-5)
                .environment(environment).agent(agent)
                .startState(MazeState.newFromXY(0,0))
                .build();
    }

    @SneakyThrows
    @Test public void whenStartingAtX3Y3_thenGoodStateValues() {
        trainer.setNofStepsBetweenUpdatedAndBackuped(ONE_STEP);
        trainer.train();
        Map<StateInterface<MazeVariables>, Double> mapOneStep= trainer.getStateValueMap();
    /*

        double avgErrOne= TestHelper.avgError(mapOneStep);

        agent.clear();
        trainer.setNofStepsBetweenUpdatedAndBackuped(THREE_STEPS);
        trainer.train();
        Map<StateInterface<MazeVariables>, Double> mapTreeSteps= trainer.getStateValueMap();
        double avgErrThree=TestHelper.avgError(mapTreeSteps);

        System.out.println("mapTreeSteps = " + mapTreeSteps);
        System.out.println("avgErrOne = " + avgErrOne+", avgErrThree = " + avgErrThree);

        Assert.assertTrue(avgErrOne>avgErrThree);


     */
    }




}
