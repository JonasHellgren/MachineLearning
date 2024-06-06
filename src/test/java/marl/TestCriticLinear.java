package marl;

import common.linear_regression_batch_fitting.DataSetCreator;
import common.other.RandUtils;
import multi_agent_rl.domain.memories.CriticLinear;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class TestCriticLinear {


    public static final int N_FEATURES = 2;
    public static final int BATCH_SIZE = 10;
    public static final double MIN_X = 0d;
    public static final double MAX_X = 1d;
    public static final int N_FITS = 1000;
    public static final double LEARNING_RATE = 1e-1;
    public static final double DELTA = 0.2;
    CriticLinear critic;

    @BeforeEach
    void init() {
        critic=new CriticLinear(N_FEATURES, LEARNING_RATE);
    }

    @Test
    void givenW0Is1AndW1Is0_whenTrained_thenCorrect() {
        BiFunction<Double,Double,Double> function = (x0, x1) -> x0 * 1 + x1 * 0;
        createDataAndFitCritic(function);
        Assertions.assertEquals(0,predict(0d, 0d), DELTA);
        Assertions.assertEquals(1,predict(1d, 0d), DELTA);

    }

    @Test
    void givenW0Is0AndW1Is1_whenTrained_thenCorrect() {
        BiFunction<Double,Double,Double> function = (x0, x1) -> x0 * 0 + x1 * 1;
        createDataAndFitCritic(function);
        Assertions.assertEquals(0,predict(0d,0d), DELTA);
        Assertions.assertEquals(1,predict(0d,1d), DELTA);
    }

    @Test
    void givenIncreasingIfCloser_whenTrained_thenCorrect() {
        int valueBias0 = 10;  //speeds up fitting
        critic=new CriticLinear(N_FEATURES, LEARNING_RATE, valueBias0);
        BiFunction<Double,Double,Double> function = (x0, x1) -> 10- x0 * 1 - x1 * 1;
        createDataAndFitCritic(function);

        System.out.println("predict(0d,0d) = " + predict(0d, 0d));
        System.out.println("predict(1d,1d) = " + predict(1d, 1d));

        Assertions.assertEquals(10,predict(0d,0d), DELTA);
        Assertions.assertEquals(8,predict(1d,1d), DELTA);
    }


    private void createDataAndFitCritic(BiFunction<Double, Double, Double> function) {
        var dataSet = getDataSet(function);
        IntStream.range(0, N_FITS).forEach((i) -> critic.fit(dataSet));
    }

    private static Pair<RealMatrix, RealVector> getDataSet(BiFunction<Double, Double, Double> function) {
        DataSetCreator creator=new DataSetCreator();
        for (int i = 0; i < BATCH_SIZE ; i++) {
            double x0= getRandomX();
            double x1= getRandomX();
            double y= function.apply(x0,x1);
            creator.addPoint(new double[]{x0,x1},y);
        }
        return creator.createDataSet();
    }

    private double predict(double x0, double x1) {
        return critic.predict(new double[]{x0, x1});
    }

    private static double getRandomX() {
        return RandUtils.getRandomDouble(MIN_X, MAX_X);
    }


}
