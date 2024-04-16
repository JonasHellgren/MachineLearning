package policygradient.maze;

import common.other.RandUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.environments.maze.MazeSettings;
import policy_gradient_problems.environments.maze.NeuralCriticMemoryMazeLossPPO;
import policy_gradient_problems.environments.maze.StateMaze;
import java.awt.geom.Point2D;

public class TestNeuralCriticMemoryMazeLossPPO {

    public static final int nRows = 100;
    public static final double TOL = 0.5;
    NeuralCriticMemoryMazeLossPPO memory;
    MazeSettings mazeSettings;

    @BeforeEach
    void init() {
        mazeSettings = MazeSettings.newDefault();
        memory=NeuralCriticMemoryMazeLossPPO.newDefault(mazeSettings);
    }

    @Test
    void whenValueIsDistToX0Y0_thenCorrect() {
        Point2D pos00=new Point2D.Double(0,0);

        double[][] inMat=new double[nRows][2];
        double[] out=new double[nRows];
        for (int i = 0; i < nRows; i++) {
            int x= RandUtils.getRandomIntNumber(0,mazeSettings.gridWidth());
            int y= RandUtils.getRandomIntNumber(0,mazeSettings.gridHeight());
            var pxy=new Point2D.Double(x,y);
            double value=pxy.distance(pos00);
            inMat[i]=new double[]{x,y};
            out[i]=value;
        }

        for (int i = 0; i < 200 ; i++) {
            memory.fit(inMat,out);
        }

        double val00=memory.getOutValue(StateMaze.newFromPoint(point(0,0)));
        double val22=memory.getOutValue(StateMaze.newFromPoint(point(2,2)));

        System.out.println("val00 = " + val00);
        System.out.println("val22 = " + val22);

        Assertions.assertEquals(val00,point(0,0).distance(pos00), TOL);
        Assertions.assertEquals(val22,point(2,2).distance(pos00),TOL);

    }

    @NotNull
    private static Point2D.Double point(double x, double y) {
        return new Point2D.Double(x, y);
    }

}
