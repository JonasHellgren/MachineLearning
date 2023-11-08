package policygradient.two_armed_bandit;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;

public class TestAddingVectors {


    @Test
    public void givenVectors_thenCorrectSum() {
        RealVector v1 = new ArrayRealVector(new double[]{1d, 2d, 1d});
        RealVector v2 = new ArrayRealVector(new double[]{1d, 2d, 1d});
        RealVector vSum = v1.add(v2);

        System.out.println("vSum = " + vSum);
        System.out.println("vSum.toArray() = " + Arrays.toString(vSum.toArray()));

        Assertions.assertTrue(Arrays.equals(new double[]{2d, 4d, 2d}, vSum.toArray()));

    }


    @Test
    public void givenVector_whenMultiply_thenCorrect() {
        RealVector v1 = new ArrayRealVector(new double[]{1d, 2d, 1d});
        RealVector vMult = v1.mapMultiplyToSelf(2);

        System.out.println("vMult = " + vMult);

        Assertions.assertTrue(Arrays.equals(new double[]{2d, 4d, 2d}, vMult.toArray()));

    }


}
