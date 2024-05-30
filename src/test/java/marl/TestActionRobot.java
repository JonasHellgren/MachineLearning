package marl;

import common.math.Discrete2DVector;
import multi_agent_rl.environments.apple.ActionRobot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestActionRobot {

    static ActionRobot north = ActionRobot.fromInt(0);
    static ActionRobot east = ActionRobot.fromInt(1);
    static ActionRobot south = ActionRobot.fromInt(2);
    static ActionRobot west = ActionRobot.fromInt(3);
    static ActionRobot stop = ActionRobot.fromInt(4);

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
