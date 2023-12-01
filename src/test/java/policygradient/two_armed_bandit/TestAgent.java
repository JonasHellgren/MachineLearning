package policygradient.two_armed_bandit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.the_problems.twoArmedBandit.AgentBandit;

public class TestAgent {

    @Test
    public void givenTheta0MuchLarger_thenAction0 () {
        var agent= AgentBandit.newWithThetas(10d,1d);
        int action=agent.chooseAction();
        Assertions.assertEquals(0,action);
    }

    @Test
    public void givenTheta1MuchLarger_thenAction1 () {
        var agent= AgentBandit.newWithThetas(1d,10d);
        int action=agent.chooseAction();
        Assertions.assertEquals(1,action);
    }


    @Test
    public void givenSimilarThetas_thenAction0or1 () {
        var agent= AgentBandit.newWithThetas(1d,1d);
        int action=agent.chooseAction();
        Assertions.assertTrue(action==0 || action==1);
    }

}
