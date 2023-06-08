package multi_step_td;

import common.RandUtils;
import multi_step_temp_diff.interfaces.EnvironmentInterface;
import multi_step_temp_diff.environments.ForkEnvironment;
import multi_step_temp_diff.models.StepReturn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestForkEnvironment {

    private static final double DELTA = 0.1;
    EnvironmentInterface environment;
    StepReturn stepReturn;

    @Before
    public void init() {
        environment=new ForkEnvironment();
    }

    @Test
    public void whenActionIs0InState0_thenState1() {
        stepReturn=environment.step(0,0);
        Assert.assertEquals(1,stepReturn.newState);
    }

    @Test
    public void whenActionIs0InState5_thenState6() {
        stepReturn=environment.step(5,0);
        Assert.assertEquals(6,stepReturn.newState);
    }

    @Test
    public void whenActionIs0Or1InState0_thenState1() {
        stepReturn=environment.step(0, RandUtils.getRandomIntNumber(0,2));
        Assert.assertEquals(1,stepReturn.newState);
    }

    @Test
    public void whenActionIs0Or1InState6_thenState11() {
        stepReturn=environment.step(6, RandUtils.getRandomIntNumber(0,2));
        Assert.assertEquals(11,stepReturn.newState);
    }

    @Test
    public void whenActionIs0Or1InState14_thenState15AndTerminalAndRewardHell() {
        stepReturn=environment.step(14, RandUtils.getRandomIntNumber(0,2));
        Assert.assertEquals(15,stepReturn.newState);
        Assert.assertTrue(stepReturn.isNewStateTerminal);
        Assert.assertEquals(ForkEnvironment.R_HELL,stepReturn.reward, DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenActionIs100InState5_thenThrow() {
        stepReturn=environment.step(100,0);
    }

}
