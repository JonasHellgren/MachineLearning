package multi_step_td.maze;

import common.ListUtils;
import common.MathUtils;
import    lombok.SneakyThrows;
import multi_step_td.TestHelper;
import multi_step_temp_diff.agents.AgentMazeTabular;
import multi_step_temp_diff.environments.*;
import multi_step_temp_diff.helpers.NStepTabularAgentTrainer;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestNStepTabularAgentTrainerMaze {

    private static final int ONE_STEP = 1;
    private static final int THREE_STEPS = 5;
    private static final int NOF_EPISODES = 100;
    public static final List<MazeState> STATES_UPPER = List.of(
            MazeState.newFromXY(0, 5), MazeState.newFromXY(1, 5), MazeState.newFromXY(2, 5), MazeState.newFromXY(3, 5));
    public static final List<MazeState> STATES_MIDDLE = List.of(
            MazeState.newFromXY(0, 3), MazeState.newFromXY(1, 3), MazeState.newFromXY(2, 3), MazeState.newFromXY(3, 3));
    public static final List<MazeState> STATES_BOTTOM = List.of(
            MazeState.newFromXY(0, 0), MazeState.newFromXY(1, 0), MazeState.newFromXY(2, 0), MazeState.newFromXY(3, 0));
    public static final List<MazeState> STATES_MERGED =
            ListUtils.merge(ListUtils.merge(STATES_UPPER, STATES_MIDDLE), STATES_BOTTOM);


    NStepTabularAgentTrainer<MazeVariables> trainer;
    AgentMazeTabular agent;

    @Before
    public void init() {
        final MazeEnvironment environment = new MazeEnvironment();
        agent = AgentMazeTabular.newDefault(environment);
        trainer= NStepTabularAgentTrainer.<MazeVariables>builder()
                .nofEpisodes(NOF_EPISODES)
                .alpha(0.5).probStart(0.9).probEnd(1e-5)
                .environment(environment).agent(agent)
                .startStateSupplier(() -> MazeState.newFromXY(0,0))
                .build();
    }

    @SneakyThrows
    @Test public void whenStartingAtX0Y5_thenGoodStateValuesAtUpperRow() {
        trainer.setStartStateSupplier(() -> MazeState.newFromXY(0,5));
        trainer.setNofStepsBetweenUpdatedAndBackuped(ONE_STEP);
        agent.clear();
        trainer.train();
        Map<StateInterface<MazeVariables>, Double> mapOneStep= trainer.getStateValueMap();
        printStateValues(STATES_UPPER,mapOneStep );
        System.out.println("---------------------------");

        double avgErrOne= TestHelper.avgErrorMaze(mapOneStep,STATES_UPPER);
        agent.clear();
        trainer.setNofStepsBetweenUpdatedAndBackuped(THREE_STEPS);
        trainer.train();
        Map<StateInterface<MazeVariables>, Double> mapTreeSteps= trainer.getStateValueMap();
        printStateValues(STATES_UPPER,mapTreeSteps );

        double avgErrThree=TestHelper.avgErrorMaze(mapTreeSteps,STATES_UPPER);
        System.out.println("mapTreeSteps = " + mapTreeSteps);
        printErrors(avgErrOne, avgErrThree);
        Assert.assertTrue(avgErrOne>avgErrThree || MathUtils.compareDoubleScalars(avgErrOne,avgErrThree,0.001));
    }


    @Test public void whenStartingAtRandom_thenGoodStateValuesAtAllCells() {
        trainer.setStartStateSupplier(MazeState::newFromRandomPos);

        agent.clear();
        trainer.setNofStepsBetweenUpdatedAndBackuped(ONE_STEP);
        trainer.train();
        Map<StateInterface<MazeVariables>, Double> mapOneSteps= trainer.getStateValueMap();
        printManyStateValues(mapOneSteps);
        double avgErrOne=TestHelper.avgErrorMaze(mapOneSteps,STATES_MERGED);
        System.out.println("---------------------------");

        agent.clear();
        trainer.setNofStepsBetweenUpdatedAndBackuped(THREE_STEPS);
        trainer.train();
        Map<StateInterface<MazeVariables>, Double> mapTreeSteps= trainer.getStateValueMap();
        printManyStateValues(mapTreeSteps);
        double avgErrThree=TestHelper.avgErrorMaze(mapTreeSteps,STATES_MERGED);

        printErrors(avgErrOne, avgErrThree);
        Assert.assertTrue(avgErrOne>avgErrThree);

    }

    private void printManyStateValues(Map<StateInterface<MazeVariables>, Double> mapTreeSteps) {
        printStateValues(STATES_UPPER, mapTreeSteps);
        printStateValues(STATES_MIDDLE, mapTreeSteps);
        printStateValues(STATES_BOTTOM, mapTreeSteps);
    }


    private static void printErrors(double avgErrOne, double avgErrThree) {
        System.out.println("avgErrOne = " + avgErrOne +", avgErrThree = " + avgErrThree);
    }

    public void printStateValues(List<MazeState> states, Map<StateInterface<MazeVariables>, Double> stateMap ) {
        for (MazeState state:states) {
            System.out.println("State = "+state+", value = "+stateMap.get(state));
        }
    }



}
