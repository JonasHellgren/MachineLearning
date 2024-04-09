package policygradient.maze;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.environments.maze.Direction;
import policy_gradient_problems.environments.maze.MazeSettings;
import policy_gradient_problems.environments.maze.RobotMover;

import java.awt.geom.Point2D;

import static org.junit.Assert.assertTrue;

public class TestRobotMoverMostlyIntendedDirection {

    public static final Action UP = Action.ofInteger(Direction.UP.getInt());
    public static final Action RIGHT = Action.ofInteger(Direction.RIGHT.getInt());
    public static final Action LEFT = Action.ofInteger(Direction.LEFT.getInt());
    public static final int N_REPS = 100;

    RobotMover mover;

    @BeforeEach
    void init() {
        MazeSettings settings = MazeSettings.newDefault();
        mover = new RobotMover(settings);
    }

    @Test
    void whenx0y0MoveUp_thenX0Y1OrX1Y0() {
        Point2D.Double pos = new Point2D.Double(0, 0);
        Point2D.Double up = new Point2D.Double(0, 1);
        Point2D.Double right = new Point2D.Double(1, 0);

        for (int i = 0; i < N_REPS; i++) {
            Point2D newPos = mover.newPos(pos, UP);
            assertTrue(newPos.equals(pos) || newPos.equals(up) || newPos.equals(right));
        }
    }


    @Test
    void whenx2y2MoveRight_thenSameOrX3Y2OrX2Y1() {
        Point2D.Double pos = new Point2D.Double(2, 2);
        Point2D.Double right = new Point2D.Double(3, 2);
        Point2D.Double down = new Point2D.Double(2, 1);
        for (int i = 0; i < N_REPS; i++) {
            Point2D newPos = mover.newPos(pos, RIGHT);
            assertTrue(newPos.equals(pos) || newPos.equals(right) || newPos.equals(down));
        }
    }

    @Test
    void when02y0MoveRandom_thenFinallyX3Y2() {
        Point2D.Double pos = new Point2D.Double(2, 2);
        while (!pos.equals(new Point2D.Double(3,2))) {
            var newPos = mover.newPos(pos, Action.ofInteger(Direction.random().getInt()));
            pos=(Point2D.Double) newPos.clone();
        }

        assertTrue(pos.equals(new Point2D.Double(3,2)));
    }

}
