package policygradient.maze;

import common.list_arrays.Array2ListConverter;
import common.other.RandUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.environments.maze.Direction;
import policy_gradient_problems.environments.maze.MazeAgentPPO;
import policy_gradient_problems.environments.maze.MazeSettings;
import policy_gradient_problems.environments.maze.StateMaze;
import java.awt.geom.Point2D;
import java.util.List;

public class TestMazeAgentPPO {
    public static final int RIGHT = Direction.RIGHT.getInt();
    public static final int UP = Direction.UP.getInt();
    public static final double ADV = 10d;
    public static final double TOL = 0.3;

    MazeAgentPPO agent;

    @BeforeEach
    void init() {
        agent = MazeAgentPPO.newDefaultAtX0Y0();
    }

    @Test
    @Disabled("long time")
    void whenFitCritic_thenCorrect() {
        Point2D pos00 = new Point2D.Double(0, 0);
        int nRows = 100;
        MazeSettings mazeSettings = MazeSettings.newDefault();

        double[][] inMat = new double[nRows][2];
        double[] out = new double[nRows];
        for (int i = 0; i < nRows; i++) {
            int x = RandUtils.getRandomIntNumber(0, mazeSettings.gridWidth());
            int y = RandUtils.getRandomIntNumber(0, mazeSettings.gridHeight());
            var pxy = new Point2D.Double(x, y);
            double value = pxy.distance(pos00);
            inMat[i] = new double[]{x, y};
            out[i] = value;
        }

        for (int i = 0; i < 200; i++) {
            agent.fitCritic(
                    Array2ListConverter.convertDoubleMatToListOfLists(inMat),
                    Array2ListConverter.convertDoubleArrToList(out));
        }

        double val00 = agent.criticOut(StateMaze.newFromPoint(point(0, 0)));
        double val22 = agent.criticOut(StateMaze.newFromPoint(point(2, 2)));

        System.out.println("val00 = " + val00);
        System.out.println("val22 = " + val22);

        Assertions.assertEquals(val00, point(0, 0).distance(pos00), TOL);
        Assertions.assertEquals(val22, point(2, 2).distance(pos00), TOL);

    }

    @Test
    @Disabled("long time")
    void whenFitActor_thenCorrect() {
        var inMat = List.of(List.of(2d, 2d), List.of(2d, 1d));

        for (int i = 0; i < 100; i++) {
            var out22 = getOutValue(inMat, 0);
            var out21 = getOutValue(inMat, 1);
            var outMat = List.of(
                    List.of((double) RIGHT, ADV, out22.get(RIGHT)),  //actionInt,adv,probOld);
                    List.of((double) UP, ADV, out21.get(UP))
            );
            agent.fitActor(inMat, outMat);
        }
        var out22 = getOutValue(inMat, 0);
        var out21 = getOutValue(inMat, 1);

        System.out.println("out22 = " + out22);
        System.out.println("out21 = " + out21);

        Assertions.assertEquals(1, out22.get(RIGHT), TOL);
        Assertions.assertEquals(1, out21.get(UP), TOL);
    }

    private List<Double> getOutValue(List<List<Double>> inMat, int i) {
        return agent.actorOut(StateMaze.newFromPoint(new Point2D.Double(
                inMat.get(i).get(0),
                inMat.get(i).get(1))));
    }

    @NotNull
    private static Point2D.Double point(double x, double y) {
        return new Point2D.Double(x, y);
    }


}
