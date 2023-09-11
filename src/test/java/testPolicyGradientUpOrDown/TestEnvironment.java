package testPolicyGradientUpOrDown;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_upOrDown.domain.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEnvironment {

    public static final double DELTA = 0.01;
    Environment environment;

    @BeforeEach
    public void init() {
        environment=Environment.newDefault();
    }

    @Test
    public void givenDefault_whenLeft_thenRewardIsOne() {
        double reward=environment.step(0);
        assertEquals(1,reward, DELTA);
    }

    @Test
    public void givenDefault_whenRight_thenRewardIsZero() {
        double reward=environment.step(1);
        assertEquals(1,reward, DELTA);
    }

}
