package testPolicyGradientUpOrDown;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_zeroOrOne.domain.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEnvironment {

    public static final double DELTA = 0.01;
    Environment environment;

    @BeforeEach
    public void init() {
        environment=Environment.newActionZeroIsGood();
    }

    @Test
    public void givenActionZeroIsGood_whenActionZero_thenRewardIsOne() {
        double reward=environment.step(0);
        assertEquals(1,reward, DELTA);
    }

    @Test
    public void givenActionZeroIsGood_whenActionOne_thenRewardIsMinusOne() {
        double reward=environment.step(1);
        assertEquals(-1,reward, DELTA);
    }
}
