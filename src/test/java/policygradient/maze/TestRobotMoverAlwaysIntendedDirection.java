package policygradient.maze;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.environments.maze.Direction;
import policy_gradient_problems.environments.maze.MazeSetting;
import policy_gradient_problems.environments.maze.RobotMover;
import java.awt.geom.Point2D;
import static org.junit.Assert.assertTrue;

public class TestRobotMoverAlwaysIntendedDirection {

    public static final Action UP = Action.ofInteger(Direction.up.getInt());
    public static final Action RIGHT = Action.ofInteger(Direction.right.getInt());
    public static final Action LEFT = Action.ofInteger(Direction.left.getInt());

    RobotMover mover;

    @BeforeEach
    void init() {
        MazeSetting settings = MazeSetting.newDefault().withProbabilityIntendedDirection(1d);
        mover=new RobotMover(settings);
    }

    @Test
    void whenx0y0MoveUp_thenY1() {
        Point2D newIntendedPos = mover.newIntendedPos(new Point2D.Double(0, 0), UP);
        assertTrue(new Point2D.Double(0, 1).equals(newIntendedPos));
    }

    @Test
    void whenx0y0MoveRight_thenX1() {
        Point2D newIntendedPos = mover.newIntendedPos(new Point2D.Double(0, 0), RIGHT);
        assertTrue(new Point2D.Double(1,0).equals(newIntendedPos));
    }

    @Test
    void whenx2y2MoveUp_thenSame() {
        Point2D.Double pos = new Point2D.Double(2, 2);
        Point2D newPos = mover.newPos(pos, UP);
        assertTrue(pos.equals(newPos));
    }


    @Test
    void whenx0y2MoveLeft_thenSame() {
        Point2D.Double pos = new Point2D.Double(0, 2);
        Point2D newPos = mover.newPos(pos,LEFT );
        assertTrue(pos.equals(newPos));
    }

    @Test
    void whenx1y0MoveUp_thenSame() {
        Point2D.Double pos = new Point2D.Double(1, 0);
        Point2D newPos = mover.newPos(pos,UP);
        assertTrue(pos.equals(newPos));
    }


}
