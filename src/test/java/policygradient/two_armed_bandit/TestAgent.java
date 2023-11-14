package policygradient.two_armed_bandit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.twoArmedBandit.Agent;

import java.util.List;

public class TestAgent {

    @Test
    public void givenTheta0MuchLarger_thenAction0 () {
        var agent= Agent.newWithThetas(10d,1d);
        int action=agent.chooseAction();
        Assertions.assertEquals(0,action);
    }

    @Test
    public void givenTheta1MuchLarger_thenAction1 () {
        var agent= Agent.newWithThetas(1d,10d);
        int action=agent.chooseAction();
        Assertions.assertEquals(1,action);
    }


    @Test
    public void givenSimilarThetas_thenAction0or1 () {
        var agent= Agent.newWithThetas(1d,1d);
        int action=agent.chooseAction();
        Assertions.assertTrue(action==0 || action==1);
    }

}