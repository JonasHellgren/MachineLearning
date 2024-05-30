package marl;

import common.math.Discrete2DPos;
import multi_agent_rl.environments.apple.AppleSettings;
import multi_agent_rl.environments.apple.StateApple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestStateApple {

    public static final Discrete2DPos POS_APPLE = Discrete2DPos.of(2, 2);
    public static final Discrete2DPos POS_A = Discrete2DPos.of(1, 2);
    public static final Discrete2DPos POS_B = Discrete2DPos.of(3, 2);
    public static final AppleSettings SETTINGS = AppleSettings.newDefault();

    StateApple state;

    @BeforeEach
    void init() {
        state= StateApple.of(POS_APPLE,POS_A,POS_B, SETTINGS);
    }

    @Test
    void whenPosA_thenCorrect() {
        Assertions.assertEquals(POS_A,state.posA());
    }

    @Test
    void whenPosB_thenCorrect() {
        Assertions.assertEquals(POS_B,state.posB());
    }

    @Test
    void whenPosApple_thenCorrect() {
        Assertions.assertEquals(POS_APPLE,state.posApple());
    }

    @Test
    void whenCopy_thenCorrect() {
        StateApple stateApple=(StateApple) state.copy();
        Assertions.assertEquals(POS_APPLE,stateApple.posApple());
    }

    @Test
    void whenPosInBounds_thenCorrect() {
        Assertions.assertTrue(StateApple.posInBounds(POS_APPLE,SETTINGS));
    }


}
