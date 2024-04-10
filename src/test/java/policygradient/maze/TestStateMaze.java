package policygradient.maze;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.environments.maze.MazeSettings;
import policy_gradient_problems.environments.maze.StateMaze;
import policy_gradient_problems.environments.maze.VariablesMaze;

import java.awt.geom.Point2D;
import java.util.List;

class TestStateMaze {


    StateMaze state;

    @BeforeEach
    void init() {
        this.state=StateMaze.newFromPoint(new Point2D.Double(0,0));
    }

    @Test
    void whenX0y0_thenCorrect() {
        Assertions.assertEquals(new Point2D.Double(0, 0), state.point());
        Assertions.assertEquals(List.of(0d, 0d), state.asList());
    }


    @Test
    void whenCopy_thenCorrect() {
        var copy=state.copy();
        copy.setVariables(new VariablesMaze(new Point2D.Double(1,1)));
        Assertions.assertEquals(new Point2D.Double(0, 0), state.point());
    }

    @Test
    void whenRandom_thenCorrect() {
        MazeSettings mazeSettings = MazeSettings.newDefault();
        var stateRandomPos=StateMaze.randomNotObstacleAndNotTerm(mazeSettings);
        System.out.println("stateRandomPos = " + stateRandomPos);
        boolean isObstaclePos=mazeSettings.isObstacle(stateRandomPos);
        boolean isTerm=mazeSettings.isTerminal(stateRandomPos);

        Assertions.assertFalse(isObstaclePos);
        Assertions.assertFalse(isTerm);

    }



}
