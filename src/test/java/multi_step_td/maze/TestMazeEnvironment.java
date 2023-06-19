package multi_step_td.maze;

import multi_step_temp_diff.environments.MazeEnvironment;
import multi_step_temp_diff.environments.MazeState;
import multi_step_temp_diff.environments.ForkVariables;
import multi_step_temp_diff.environments.MazeVariables;
import multi_step_temp_diff.interfaces_and_abstract.EnvironmentInterface;
import multi_step_temp_diff.models.StepReturn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestMazeEnvironment {

    public static final double DELTA = 0.01;
    EnvironmentInterface<MazeVariables> environment;
    StepReturn<MazeVariables> stepReturn;

    @Before
    public void init() {
        environment=new MazeEnvironment();
    }

    @Test
    public void givenx0y0_whenActionUp_thenOtherPosAndZeroReward() {
        stepReturn=environment.step(MazeState.newFromXY(0,0),MazeEnvironment.ACTION_UP);
        printStepReturn();
        Assert.assertEquals(MazeState.newFromXY(0,1),stepReturn.newState);
        Assert.assertEquals(MazeEnvironment.REWARD_MOVE,stepReturn.reward, DELTA);
        Assert.assertFalse(stepReturn.isNewStateTerminal);

    }

    @Test
    public void givenx0y0_whenActionR_thenOtherPosAndZeroReward() {
        stepReturn=environment.step(MazeState.newFromXY(0,0),MazeEnvironment.ACTION_R);
        printStepReturn();
        Assert.assertEquals(MazeState.newFromXY(1,0),stepReturn.newState);
        Assert.assertEquals(MazeEnvironment.REWARD_MOVE,stepReturn.reward, DELTA);
    }

    @Test
    public void givenx0y0_whenActionDown_thenStillAndCrashReward() {
        stepReturn=environment.step(MazeState.newFromXY(0,0),MazeEnvironment.ACTION_DOWN);
        printStepReturn();
        Assert.assertEquals(MazeState.newFromXY(0,0),stepReturn.newState);
        Assert.assertEquals(MazeEnvironment.REWARD_CRASH,stepReturn.reward, DELTA);
    }

    @Test
    public void givenx0y0_whenActionL_thenStillAndCrashReward() {
        stepReturn=environment.step(MazeState.newFromXY(0,0),MazeEnvironment.ACTION_L);
        printStepReturn();
        Assert.assertEquals(MazeState.newFromXY(0,0),stepReturn.newState);
        Assert.assertEquals(MazeEnvironment.REWARD_CRASH,stepReturn.reward, DELTA);
    }

    @Test
    public void givenx4y4_whenActionL_thenOtherPosAndZeroReward() {
        stepReturn=environment.step(MazeState.newFromXY(4,4),MazeEnvironment.ACTION_L);
        printStepReturn();
        Assert.assertEquals(MazeState.newFromXY(3,4),stepReturn.newState);
        Assert.assertEquals(MazeEnvironment.REWARD_MOVE,stepReturn.reward, DELTA);
    }

    @Test
    public void givenx4y4_whenActionUp_thenOtherPosAndGoalReward() {
        stepReturn=environment.step(MazeState.newFromXY(4,4),MazeEnvironment.ACTION_UP);
        printStepReturn();
        Assert.assertEquals(MazeState.newFromXY(4,5),stepReturn.newState);
        Assert.assertEquals(MazeEnvironment.REWARD_GOAL,stepReturn.reward, DELTA);
        Assert.assertTrue(stepReturn.isNewStateTerminal);
    }

    private void printStepReturn() {
        System.out.println("stepReturn = " + stepReturn);
    }


}
