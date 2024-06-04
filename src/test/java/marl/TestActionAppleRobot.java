package marl;

import common.math.Discrete2DVector;
import multi_agent_rl.environments.apple.ActionAppleRobot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestActionAppleRobot {

    static ActionAppleRobot north = ActionAppleRobot.fromInt(0);
    static ActionAppleRobot east = ActionAppleRobot.fromInt(1);
    static ActionAppleRobot south = ActionAppleRobot.fromInt(2);
    static ActionAppleRobot west = ActionAppleRobot.fromInt(3);
    static ActionAppleRobot stop = ActionAppleRobot.fromInt(4);

    @Test
    void whenNorth_thenCorrect() {
        Assertions.assertTrue(north.getDirection().equals(Discrete2DVector.of(0,1)));
    }

    @Test
    void whenEast_thenCorrect() {
        Assertions.assertTrue(east.getDirection().equals(Discrete2DVector.of(1,0)));
    }

    @Test
    void whenSouth_thenCorrect() {
        Assertions.assertTrue(south.getDirection().equals(Discrete2DVector.of(0,-1)));
    }

    @Test
    void whenWest_thenCorrect() {
        Assertions.assertTrue(west.getDirection().equals(Discrete2DVector.of(-1,0)));
    }

    @Test
    void whenStop_thenCorrect() {
        Assertions.assertTrue(stop.getDirection().equals(Discrete2DVector.of(0,0)));
    }

}
