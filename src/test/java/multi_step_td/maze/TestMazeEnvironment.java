package multi_step_td.maze;

import multi_step_temp_diff.environments.MazeEnvironment;
import multi_step_temp_diff.environments.MazeState;
import multi_step_temp_diff.environments.ForkVariables;
import multi_step_temp_diff.environments.MazeVariables;
import multi_step_temp_diff.interfaces_and_abstract.EnvironmentInterface;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.StepReturn;
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
        assertEquals(MazeEnvironment.REWARD_MOVE,stepReturn.reward, DELTA);
        assertFalse(stepReturn.isNewStateTerminal);

    }

    @Test
    public void givenx0y0_whenActionR_thenOtherPosAndZeroReward() {
        stepReturn=environment.step(MazeState.newFromXY(0,0),MazeEnvironment.ACTION_R);
        printStepReturn();
        assertEquals(MazeState.newFromXY(1,0),stepReturn.newState);
        assertEquals(MazeEnvironment.REWARD_MOVE,stepReturn.reward, DELTA);
    }

    @Test
    public void givenx0y0_whenActionDown_thenStillAndCrashReward() {
        stepReturn=environment.step(MazeState.newFromXY(0,0),MazeEnvironment.ACTION_DOWN);
        printStepReturn();
        assertEquals(MazeState.newFromXY(0,0),stepReturn.newState);
        assertEquals(MazeEnvironment.REWARD_CRASH,stepReturn.reward, DELTA);
    }

    @Test
    public void givenx0y0_whenActionL_thenStillAndCrashReward() {
        stepReturn=environment.step(MazeState.newFromXY(0,0),MazeEnvironment.ACTION_L);
        printStepReturn();
        assertEquals(MazeState.newFromXY(0,0),stepReturn.newState);
        assertEquals(MazeEnvironment.REWARD_CRASH,stepReturn.reward, DELTA);
    }

    @Test
    public void givenx4y4_whenActionL_thenOtherPosAndZeroReward() {
        stepReturn=environment.step(MazeState.newFromXY(4,4),MazeEnvironment.ACTION_L);
        printStepReturn();
        assertEquals(MazeState.newFromXY(3,4),stepReturn.newState);
        assertEquals(MazeEnvironment.REWARD_MOVE,stepReturn.reward, DELTA);
    }

    @Test
    public void givenx4y4_whenActionUp_thenOtherPosAndGoalReward() {
        stepReturn=environment.step(MazeState.newFromXY(4,4),MazeEnvironment.ACTION_UP);
        printStepReturn();
        assertEquals(MazeState.newFromXY(4,5),stepReturn.newState);
        assertEquals(MazeEnvironment.REWARD_GOAL+MazeEnvironment.REWARD_MOVE,stepReturn.reward, DELTA);
        assertTrue(stepReturn.isNewStateTerminal);
    }

    @Test
    public void okStateSet() {
        Set<StateInterface<MazeVariables>>  stateSet=environment.stateSet();
        assertEquals(MazeEnvironment.NOF_COLS*MazeEnvironment.NOF_ROWS,stateSet.size());
    }

    private void printStepReturn() {
        System.out.println("stepReturn = " + stepReturn);
    }


}
