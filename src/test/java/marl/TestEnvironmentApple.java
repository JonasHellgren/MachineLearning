package marl;

import common.math.Discrete2DPos;
import common.math.Discrete2DVector;
import multi_agent_rl.environments.apple.AppleSettings;
import multi_agent_rl.environments.apple.EnvironmentApple;
import multi_agent_rl.environments.apple.StateApple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestEnvironmentApple {


    public static final Discrete2DPos POS_APPLE = Discrete2DPos.of(2, 2);
    EnvironmentApple environment;
    AppleSettings settings=AppleSettings.newDefault();

    @BeforeEach
    void init() {
        environment=new EnvironmentApple(settings);
    }

    @Test
    void whenNorth_thenCorrect() {
        var state= StateApple.of(POS_APPLE,Discrete2DPos.of(1,2),Discrete2DPos.of(3,2),settings);
//        Assertions.assertTrue(north.getDirection().equals(Discrete2DVector.of(0,1)));
    }


}
