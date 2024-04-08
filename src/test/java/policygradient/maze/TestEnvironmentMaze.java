package policygradient.maze;

import common.MathUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.value_classes.StepReturn;
import policy_gradient_problems.environments.maze.*;
import java.awt.geom.Point2D;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestEnvironmentMaze {

    public static final int N_REPS = 100;
    public static final double DELTA = 1e-4;
    public static final Action UP = Action.ofInteger(Direction.up.getInt());


    EnvironmentMaze environment;
    StateMaze state;
    MazeSettings settings;

    @BeforeEach
    void init() {
        settings = MazeSettings.newDefault();
        environment=new EnvironmentMaze(settings);
        state=StateMaze.newFromPoint(new Point2D.Double(0,0));
    }

    @Test
    void whenx0y0MoveUp_thenX0Y1OrX1Y0() {
        var pos = new Point2D.Double(0, 0);
        var up = new Point2D.Double(0, 1);
        var right = new Point2D.Double(1, 0);

        for (int i = 0; i < N_REPS; i++) {
            var sr = environment.step(state, Action.ofInteger(Direction.random().getInt()));
            var point = getPoint(sr);
            assertTrue(point.equals(pos) || point.equals(up) || point.equals(right));
            assertEquals(-settings.costMove(), sr.reward(), DELTA);
        }
    }

    @Test
    void whenx2y1MoveUp_thenSameOrX3Y2OrX2Y1() {
        Point2D.Double pos = new Point2D.Double(2, 1);
        state.setPoint(pos);
        Point2D.Double right = new Point2D.Double(3, 1);
        Point2D.Double up = new Point2D.Double(2, 2);
        for (int i = 0; i < N_REPS; i++) {
            var sr = environment.step(state, UP);
            var newPoint = getPoint(sr);
            assertTrue(newPoint.equals(pos) || newPoint.equals(right) || newPoint.equals(up));
            assertTrue(MathUtils.isZero(-settings.costMove()-sr.reward()) ||
                    MathUtils.isZero(settings.rewardTerminalBad()-sr.reward())) ;
        }
    }

    @Test
    void whenx0y0MoveManyRandom_thenEndingTerminal() {

        boolean isTerminal=false;
        while (!isTerminal) {
            var sr = environment.step(state, Action.ofInteger(Direction.random().getInt()));
            var point = getPoint(sr);
            state.setPoint(point);
            isTerminal=sr.isTerminal();
        }

        assertTrue(state.point().equals(settings.posTerminalGood()) ||
                        state.point().equals(settings.posTerminalBad()));

    }



    @Test
    void whenX2y1MoveUp_thenX2Y2OrTermBad() {
        Point2D.Double pos = new Point2D.Double(2, 1);
        state.setVariables(new VariablesMaze(pos));
        Point2D.Double right = new Point2D.Double(3, 1);
        Point2D.Double up = new Point2D.Double(2, 2);
        for (int i = 0; i < N_REPS; i++) {
            var sr = environment.step(state, UP);
            var newPoint = getPoint(sr);
            assertTrue(newPoint.equals(pos) || newPoint.equals(right) || newPoint.equals(up));
            assertTrue(MathUtils.isZero(-settings.costMove()-sr.reward()) ||
                    MathUtils.isZero(-settings.rewardTerminalGood()-sr.reward())) ;
        }
    }




    private static Point2D getPoint(StepReturn<VariablesMaze> sr) {
        StateMaze sm=(StateMaze) sr.state();
        return sm.point();
    }



}
