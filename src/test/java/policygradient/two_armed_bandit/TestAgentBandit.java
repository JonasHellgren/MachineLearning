package policygradient.two_armed_bandit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.environments.twoArmedBandit.AgentBanditParamActor;

 class TestAgentBandit {

    @Test
     void givenTheta0MuchLarger_thenAction0 () {
        var agent= AgentBanditParamActor.newWithThetas(10d,1d);
        int action=agent.chooseAction().asInt();
        Assertions.assertEquals(0,action);
    }

    @Test
     void givenTheta1MuchLarger_thenAction1 () {
        var agent= AgentBanditParamActor.newWithThetas(1d,10d);
        int action=agent.chooseAction().asInt();
        Assertions.assertEquals(1,action);
    }


    @Test
     void givenSimilarThetas_thenAction0or1 () {
        var agent= AgentBanditParamActor.newWithThetas(1d,1d);
        int action=agent.chooseAction().asInt();
        Assertions.assertTrue(action==0 || action==1);
    }

}
