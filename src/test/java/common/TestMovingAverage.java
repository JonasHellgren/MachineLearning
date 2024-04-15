package common;

import common.math.MovingAverage;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestMovingAverage {

    private static final List<Double> threeOnes = Arrays.asList(1d, 1d, 1d);
    private static final List<Double> fiveZerosFiveOnes = Arrays.asList(0d, 0d, 0d,0d, 0d, 1d, 1d, 1d,1d,1d);
    private static final double DELTA = 0.1;
    MovingAverage movingAverage;

    @Test
    public void givenThreeOnes_whenLenght1_thenMAVGisThreeOnes () {
        movingAverage=new MovingAverage(1,threeOnes );
        printFiltered();
        Assert.assertEquals(Arrays.asList(1d,1d,1d),movingAverage.getFiltered());
    }

    @Test
    public void givenThreeOnes_whenLenght3_thenMAVGisThreeOnes () {
        movingAverage=new MovingAverage(3, threeOnes);
        printFiltered();
        Assert.assertEquals(Arrays.asList(1d,1d,1d),movingAverage.getFiltered());
    }

    @Test
    public void givenFiveZeroAndFiveOnes_whenLenght3_thenMAVGisThreeOnes () {
        movingAverage=new MovingAverage(3, fiveZerosFiveOnes);
        printFiltered();
        Assert.assertTrue(movingAverage.getFiltered().stream().anyMatch(v -> Math.abs(v-0.333)< DELTA));
        Assert.assertTrue(movingAverage.getFiltered().stream().anyMatch(v -> Math.abs(v-0.666)< DELTA));

    }


    private void printFiltered() {
        System.out.println("filtered = " + movingAverage.getFiltered());
    }


}
