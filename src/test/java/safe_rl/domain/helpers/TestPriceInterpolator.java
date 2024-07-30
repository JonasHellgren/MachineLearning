package safe_rl.domain.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.environment.helpers.PriceInterpolator;

import static common.list_arrays.ArrayUtil.isDoubleArraysEqual;

public class TestPriceInterpolator {

    PriceInterpolator interpolator;

    /**
     * price 1 is at time 0, price 3 at time 2 and
     * price 5 at time 4
     */

    @BeforeEach
    void init() {
        interpolator=new PriceInterpolator(1,new double[]{1,2,3,4,5});
    }


    @Test
    void whenCreated_thenCorrectTimes() {
        double[] times= interpolator.getTimes();
        Assertions.assertTrue(isDoubleArraysEqual(new double[]{0,1,2,3,4},times,1e-5));
    }


    @Test
    void whenTim0_thenCorrect() {
        double price= interpolator.priceAtTime(0);
        Assertions.assertEquals(1d,price);
    }

    @Test
    void whenTim0dot5_thenCorrect() {
        double price= interpolator.priceAtTime(0.5);
        Assertions.assertEquals(1.5d,price);
    }

    @Test
    void whenTime3_thenCorrect() {
        double price= interpolator.priceAtTime(2);
        Assertions.assertEquals(3d,price);
    }

    @Test
    void whenTime4_thenCorrect() {
        double price= interpolator.priceAtTime(4);
        Assertions.assertEquals(5d,price);
    }

    @Test
    void whenTime5_thenOutsideThenCorrect() {
        double price= interpolator.priceAtTime(5);
        Assertions.assertEquals(5d,price);
    }

    @Test
    void whenTimeNegative_thenOutsideThenCorrect() {
        double price= interpolator.priceAtTime(-1);
        Assertions.assertEquals(1,price);
    }


}

