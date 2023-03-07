package mcts_cell_charging;

import common.Interpolator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class TestOCVInterpolation {


    private static final double DELTA = 0.1;
    double SOC_L = 0.2, SOC_H = 0.8;
    double OCV_0 = 1, OCV_L = 2.4, OCV_H = 2.5, OCV_1 = 4;
    double[] X, Y;

    Interpolator interpolator;

    @Before
    public void init() {
        X = new double[]{0, SOC_L, SOC_H, 1};
        Y = new double[]{OCV_0, OCV_L, OCV_H, OCV_1};
        this.interpolator = new Interpolator(X, Y);
    }

    @Test
    public void whenSoC0_thenOCV1() {
        double[] ocv= interpolator.interpLinear(new double[]{0d});
        Assert.assertEquals(OCV_0, ocv[0], DELTA);
    }

    @Test
    public void whenSoC0d1_thenOCV1d7() {
        double[] ocv= interpolator.interpLinear(new double[]{0.1d});
        Assert.assertEquals((OCV_0+OCV_L)/2, ocv[0], DELTA);
    }

    @Test
    public void whenSoC0d5_thenOCV1d7() {
        double[] ocv= interpolator.interpLinear(new double[]{0.5d});
        Assert.assertEquals((OCV_L+OCV_H)/2, ocv[0], DELTA);
    }

    @Test
    public void whenSoCH_thenOCVH() {
        double[] ocv= interpolator.interpLinear(new double[]{SOC_H});
        Assert.assertEquals(OCV_H, ocv[0], DELTA);
    }

    @Test
    public void whenSoC1_thenOCV1() {
        double[] ocv= interpolator.interpLinear(new double[]{1});
        Assert.assertEquals(OCV_1, ocv[0], DELTA);
    }

}
