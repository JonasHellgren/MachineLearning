package multi_step_td.maze;

import multi_step_temp_diff.domain.environments.maze.MazeEnvironment;
import multi_step_temp_diff.domain.environments.maze.MazeState;
import multi_step_temp_diff.domain.environments.maze.MazeVariables;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestMazeEnvironment {

    public static final double DELTA = 0.01;
    EnvironmentInterface<MazeVariables> environment;
    StepReturn<MazeVariables> stepReturn;

    @BeforeEach
    public void init() {
        environment=new MazeEnvironment();
    }

    @Test
    public void givenx0y0_whenActionUp_thenOtherPosAndZeroReward() {
        stepReturn=environment.step(MazeState.newFromXY(0,0),MazeEnvironment.ACTION_UP);
        printStepReturn();
        assertEquals(MazeState.newFromXY(0,1),stepReturn.newState);
        assertEquals(MazeEnvironment.settings.rewardMove(),stepReturn.reward, DELTA);
        assertFalse(stepReturn.isNewStateTerminal);

    }

    @Test
    public void givenx0y0_whenActionR_thenOtherPosAndZeroReward() {
        stepReturn=environment.step(MazeState.newFromXY(0,0),MazeEnvironment.ACTION_R);
        printStepReturn();
        assertEquals(MazeState.newFromXY(1,0),stepReturn.newState);
        assertEquals(MazeEnvironment.settings.rewardMove(),stepReturn.reward, DELTA);
    }

    @Test
    public void givenx0y0_whenActionDown_thenStillAndCrashReward() {
        stepReturn=environment.step(MazeState.newFromXY(0,0),MazeEnvironment.ACTION_DOWN);
        printStepReturn();
        assertEquals(MazeState.newFromXY(0,0),stepReturn.newState);
        assertEquals(MazeEnvironment.settings.rewardCrash(),stepReturn.reward, DELTA);
    }

    @Test
    public void givenx0y0_whenActionL_thenStillAndCrashReward() {
        stepReturn=environment.step(MazeState.newFromXY(0,0),MazeEnvironment.ACTION_L);
        printStepReturn();
        assertEquals(MazeState.newFromXY(0,0),stepReturn.newState);
        assertEquals(MazeEnvironment.settings.rewardCrash(),stepReturn.reward, DELTA);
    }

    @Test
    public void givenx4y4_whenActionL_thenOtherPosAndZeroReward() {
        stepReturn=environment.step(MazeState.newFromXY(4,4),MazeEnvironment.ACTION_L);
        printStepReturn();
        assertEquals(MazeState.newFromXY(3,4),stepReturn.newState);
        assertEquals(MazeEnvironment.settings.rewardMove(),stepReturn.reward, DELTA);
    }

    @Test
    public void givenx4y4_whenActionUp_thenOtherPosAndGoalReward() {
        stepReturn=environment.step(MazeState.newFromXY(4,4),MazeEnvironment.ACTION_UP);
        printStepReturn();
        assertEquals(MazeState.newFromXY(4,5),stepReturn.newState);
        assertEquals(MazeEnvironment.settings.rewardGoal()+MazeEnvironment.settings.rewardMove(),stepReturn.reward, DELTA);
        assertTrue(stepReturn.isNewStateTerminal);
    }

    @Test
    public void whenTakingFewSteps_thenNotTerminal() {

        MazeState state = MazeState.newFromXY(1, 1);
        for (int i = 0; i < 10 ; i++) {
            stepReturn=environment.step(state,MazeEnvironment.ACTION_UP);
            state.setFromReturn(stepReturn);
        }
        assertFalse(stepReturn.isNewStateTerminal);
    }

    @Test
    public void whenTakingManySteps_thenTerminal() {

        MazeState state = MazeState.newFromXY(4, 4);
        for (int i = 0; i < 10000 ; i++) {
            stepReturn=environment.step(state,MazeEnvironment.ACTION_UP);
        }
        assertTrue(stepReturn.isNewStateTerminal);
    }

    @Test
    public void okStateSet() {
        Set<StateInterface<MazeVariables>>  stateSet=environment.stateSet();
        assertEquals(MazeEnvironment.settings.nofCols()*MazeEnvironment.settings.nofRows(),stateSet.size());
    }

    private void printStepReturn() {
        System.out.println("stepReturn = " + stepReturn);
    }


}
