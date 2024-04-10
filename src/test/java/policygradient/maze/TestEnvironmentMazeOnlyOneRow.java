package policygradient.maze;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.value_classes.StepReturn;
import policy_gradient_problems.environments.maze.*;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestEnvironmentMazeOnlyOneRow {

    public static final int N_REPS = 100;
    public static final double DELTA = 1e-4;
    public static final Action UP = Action.ofInteger(Direction.UP.getInt());


    EnvironmentMaze environment;
    StateMaze state;
    MazeSettings settings;

    @BeforeEach
    void init() {
        settings = MazeSettings.newOneRowMoveAsIntended();
        environment=new EnvironmentMaze(settings);
        state=StateMaze.newFromPoint(new Point2D.Double(0,0));
    }

    @Test
    void whenx0y0MoveUp_thenX0Y0OrX1Y0() {
        var pos = new Point2D.Double(0, 0);
        var up = new Point2D.Double(0, 0);
        var right = new Point2D.Double(1, 0);

        for (int i = 0; i < N_REPS; i++) {
            var sr = environment.step(state, Action.ofInteger(Direction.random().getInt()));
            var point = getPoint(sr);
            assertTrue(point.equals(pos) || point.equals(up) || point.equals(right));
            assertEquals(-settings.costMove(), sr.reward(), DELTA);
        }
    }

    @Test
    void whenx0y0MoveManyRandom_thenEndingGoodTerminal() {

        boolean isTerminal=false;
        while (!isTerminal) {
            var sr = environment.step(state, Action.ofInteger(Direction.random().getInt()));
            var point = getPoint(sr);
            state.setPoint(point);
            System.out.println("point = " + point);
            isTerminal=sr.isTerminal();
        }

        Assertions.assertNotEquals(state.point(), settings.posTerminalBad());
        assertEquals(state.point(), settings.posTerminalGood());

    }



    private static Point2D getPoint(StepReturn<VariablesMaze> sr) {
        StateMaze sm=(StateMaze) sr.state();
        return sm.point();
    }


}
