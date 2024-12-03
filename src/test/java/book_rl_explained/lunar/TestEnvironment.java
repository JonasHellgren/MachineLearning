package book_rl_explained.lunar;

import book_rl_explained.lunar_lander.domain.environment.EnvironmentLunar;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestEnvironment {

    static final double TOL = 0.01;
    EnvironmentLunar environment;
    LunarProperties properties;

    @BeforeEach
    void setUp() {
        environment = EnvironmentLunar.createDefault();
        properties = environment.getProps();
    }

    @Test
    void testStep_NotTerminal_NotFail() {
        var state = StateLunar.of(10.0, 0.0);
        double action = 0.0;
        var stepReturn = environment.step(state, action);

        assertFalse(stepReturn.isTerminal());
        assertFalse(stepReturn.isFail());
        assertEquals(properties.rewardStep(), stepReturn.reward(), TOL);
    }

    @Test
    void testStep_Terminal_NotFail() {
        var state = StateLunar.of(0.0, 0.0);
        double action = 0.0;
        var stepReturn = environment.step(state, action);

        assertTrue(stepReturn.isTerminal());
        assertFalse(stepReturn.isFail());
        assertEquals(properties.rewardStep(), stepReturn.reward(), TOL);
    }

    @Test
    void testStep_Terminal_Fail() {
        var state = StateLunar.of(1.0, 0.0);
        double action = -properties.forceMax();
        var stepReturn = environment.step(state, action);
        assertTrue(stepReturn.isTerminal());
        assertTrue(stepReturn.isFail());
        assertEquals(properties.rewardFail() + properties.rewardStep(), stepReturn.reward(), TOL);
    }

    @Test
    void testStep_ForceClipped() {
        var state = StateLunar.of(10e3, 0.0);
        var stepReturn1 = environment.step(state, properties.forceMax());
        var stepReturn2 = environment.step(state, properties.forceMax() * 2);
        assertEquals(stepReturn1.stateNew().y(), stepReturn2.stateNew().y());
        assertEquals(stepReturn1.stateNew().spd(), stepReturn2.stateNew().spd());
    }

}
