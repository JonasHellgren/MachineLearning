package testPolicyGradientUpOrDown;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_upOrDown.domain.Agent;
import policy_gradient_upOrDown.domain.Environment;

public class TestAgent {

    public static final double DELTA = 0.01;
    Agent agent;

    @BeforeEach
    public void init() {
        agent=Agent.builder().build();
    }

    @Test
    void givenDefault_whenChooseAction_thenZeroOrOne() {
        int action=agent.chooseAction();
        Assertions.assertTrue(action==0 || action==1);
    }



    @Test
    void givenLargeThen_whenChooseAction_thenOne() {
        agent= Agent.builder().theta(Double.MAX_VALUE).build();
        int action=agent.chooseAction();
        Assertions.assertEquals(1,action);
    }

    @Test
    void givenSmallThen_whenChooseAction_thenZero() {
        agent= Agent.builder().theta(-Double.MAX_VALUE).build();
        int action=agent.chooseAction();
        Assertions.assertEquals(0,action);
    }

}
