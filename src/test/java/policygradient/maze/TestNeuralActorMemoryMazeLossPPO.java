package policygradient.maze;

import common_dl4j.Dl4JUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import policy_gradient_problems.environments.maze.Direction;
import policy_gradient_problems.environments.maze.MazeSettings;
import policy_gradient_problems.environments.maze.NeuralActorMemoryMazeLossPPO;

import java.util.Arrays;

public class TestNeuralActorMemoryMazeLossPPO {

    public static final double TOL = 1e-3;
    public static final int RIGHT = Direction.RIGHT.getInt();
    public static final int UP = Direction.UP.getInt();
    public static final double ADV = 10d;

    NeuralActorMemoryMazeLossPPO memory;

    @BeforeEach
    void init() {
        memory = NeuralActorMemoryMazeLossPPO.newDefault(MazeSettings.newDefault());
    }

    @Test
    void whenOnHot_thenCorrect() {
        INDArray oneHot = Dl4JUtil.createOneHot(4, 1);
        Assertions.assertTrue(oneHot.isVector());
        Assertions.assertEquals(1d, oneHot.sumNumber().doubleValue());
        Assertions.assertEquals(4, oneHot.size(0));
        Assertions.assertEquals(1, oneHot.rank());
        Assertions.assertEquals(1d, oneHot.toDoubleVector()[1]);
        Assertions.assertEquals(0d, oneHot.toDoubleVector()[0]);
    }

    @Test
    void whenNotTrained_thenCorrectOutShape() {
        double[] out = memory.getOutValue(new double[]{0d, 0d});
        Assertions.assertEquals(4, out.length);
        Assertions.assertEquals(1, Arrays.stream(out).sum(), TOL);
    }

    @Test
    @Disabled("long time")
    void whenFitX2Y2RightActionAndX2Y1UpAction_thenCorrect() {
        double[][] inMat = {{2d, 2d},{2d, 1d}};

        for (int i = 0; i < 100; i++) {
            double[] out22 = getOutValue(inMat, 0);
            double[] out21 = getOutValue(inMat, 1);
            double[][] outMat = {{RIGHT, ADV, out22[RIGHT]},{UP, ADV, out21[UP]}}; //actionInt,adv,probOld);
            memory.fit(inMat, outMat);
        }
        double[] out22After = getOutValue(inMat, 0);
        double[] out21After = getOutValue(inMat, 1);
        System.out.println("out22 = " + Arrays.toString(out22After));
        System.out.println("out21 = " + Arrays.toString(out21After));

        Assertions.assertEquals(1, out22After[RIGHT], 0.3);
        Assertions.assertEquals(1, out21After[UP], 0.3);

    }

    private double[] getOutValue(double[][] inMat, int i) {
        return memory.getOutValue(inMat[i]);
    }


}
