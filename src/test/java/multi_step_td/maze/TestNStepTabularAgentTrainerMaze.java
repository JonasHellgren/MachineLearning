package multi_step_td.maze;

import    lombok.SneakyThrows;
import multi_step_td.TestHelper;
import multi_step_temp_diff.agents.AgentMazeTabular;
import multi_step_temp_diff.environments.*;
import multi_step_temp_diff.helpers.NStepTabularAgentTrainer;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class TestNStepTabularAgentTrainerMaze {

    private static final int ONE_STEP = 1;
    private static final int THREE_STEPS = 3;
    private static final int NOF_EPISODES = 10;
    public static final List<MazeState> STATES_UPPER = List.of(
            MazeState.newFromXY(1, 5), MazeState.newFromXY(2, 5), MazeState.newFromXY(3, 5));
    NStepTabularAgentTrainer<MazeVariables> trainer;
    AgentMazeTabular agent;

    @Before
    public void init() {
        final MazeEnvironment environment = new MazeEnvironment();
        agent = AgentMazeTabular.newDefault(environment);
        trainer= NStepTabularAgentTrainer.<MazeVariables>builder()
                .nofEpisodes(NOF_EPISODES)
                .alpha(0.2).probStart(0.9).probEnd(1e-5)
                .environment(environment).agent(agent)
                .startState(MazeState.newFromXY(0,0))
                .build();
    }

    @SneakyThrows
    @Test public void whenStartingAtX0Y5_thenGoodStateValues() {
        trainer.setStartState(MazeState.newFromXY(0,5));

       trainer.setNofStepsBetweenUpdatedAndBackuped(ONE_STEP);

        agent.clear();
        trainer.train();
        Map<StateInterface<MazeVariables>, Double> mapOneStep= trainer.getStateValueMap();

        printStateValues(STATES_UPPER,mapOneStep );

        double avgErrOne= TestHelper.avgErrorMaze(mapOneStep);


       // double avgErrOne=0;

        agent.clear();
        trainer.setNofStepsBetweenUpdatedAndBackuped(THREE_STEPS);
        trainer.train();
        Map<StateInterface<MazeVariables>, Double> mapTreeSteps= trainer.getStateValueMap();
        printStateValues(STATES_UPPER,mapTreeSteps );

        double avgErrThree=TestHelper.avgErrorMaze(mapTreeSteps);

        System.out.println("mapTreeSteps = " + mapTreeSteps);
        System.out.println("avgErrOne = " + avgErrOne+", avgErrThree = " + avgErrThree);

        Assert.assertTrue(avgErrOne>avgErrThree);

    }


    public void printStateValues(List<MazeState> states, Map<StateInterface<MazeVariables>, Double> stateMap ) {
        for (MazeState state:states) {
            System.out.println("State = "+state+", value = "+stateMap.get(state));
        }
    }



}
