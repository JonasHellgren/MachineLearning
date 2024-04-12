package common_dl4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.factory.Nd4j;

import java.util.List;

/**
 * Using data from example in entropy.md
 */

public class TestEntropyCalculatorDiscreteActions {
    public static final double DELTA = 1e-3;

    @Test
     void givenTEqualToTP_thenZeroCrossEntropy() {
        var TP= Nd4j.create(List.of(0,1));
        var P= Nd4j.create(List.of(0,1));
        double ce= CrossEntropyCalculator.calcCrossEntropy(TP,P);
        Assertions.assertEquals(0,ce, DELTA);
    }

    @Test
     void givenTE1Div7_thenCorrectCrossEntropy() {
        var TP= Nd4j.create(List.of(0,1));
        var P= Nd4j.create(List.of(1d/7,1d-1d/7));
        double ce= CrossEntropyCalculator.calcCrossEntropy(TP,P);
        Assertions.assertEquals(0.1541,ce, DELTA);  //calculated in matlab
    }

    @Test
     void givenTEZero_thenCorrectEntropy() {
        var P= Nd4j.create(List.of(0,1));
        double ce= new EntropyCalculatorDiscreteActions().calcEntropy(P);
        Assertions.assertEquals(0.0,ce, DELTA);  //calculated in matlab
    }


    @Test
     void givenTE1Div7_thenCorrectEntropy() {
        var P= Nd4j.create(List.of(1d/7,1d-1d/7));
        double ce= new EntropyCalculatorDiscreteActions().calcEntropy(P);
        Assertions.assertEquals(0.41,ce, DELTA);  //calculated in matlab
    }


}
