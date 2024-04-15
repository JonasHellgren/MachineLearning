package common;

import common.math.LinearFitter;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

public class TestLinearFitter {
    public static final int K = 5;


    LinearFitter fitter;

    double[] yArr;
    double[][] xMat;
    final int nDim = 1;


    @BeforeEach
    void init() {
        fitter=LinearFitter.ofLearningRateAndNDim(0.9,1);
        var xList = ListUtils.createDoubleListStartEndStep(0, 1, 0.01);
        int nPoints = xList.size();
        yArr = new double[nPoints];
        xMat = new double[nPoints][nDim];
        for (int i = 0; i < nPoints; i++) {
            double x = xList.get(i);
            xMat[i][nDim - 1] = x;
            yArr[i] = x * K;
        }
    }

    @Test
    void whenDataPoint_thenCorrect() {
        Pair<Double, double[]> point= randomDataPoint();
        double[] x =point.getSecond();
        double y=point.getFirst();
        System.out.println("x = " + x[0] + ", y = " + y);
        Assertions.assertEquals(y, x[0] * K);
    }

    @Test
    void whenFitting_thenCorrect() {
        int nFits = 100;
        for (int i = 0; i < nFits; i++) {
        fitter.fit(randomDataPoint());
        }

        int i = RandUtils.getRandomIntNumber(0, yArr.length);
        double y=yArr[i];
        double yPred=fitter.predict(xMat[i]);

        System.out.println("x = " + Arrays.toString(xMat[i])+", y = " + y+", yPred = " + yPred);
        Assertions.assertEquals(y,yPred,1e-1);

    }



        Pair<Double, double[]> randomDataPoint() {

        int i = RandUtils.getRandomIntNumber(0, yArr.length);
        double[] x = new double[nDim];
        x[0] = xMat[i][0];
        double y = yArr[i];
        return Pair.create(y, x);
    }


}
