package common_dl4j;

import common.dl4j.EntropyCalculatorContActions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestEntropyCalculatorContActions {


    EntropyCalculatorContActions calculator;

    @BeforeEach
    void init() {
        calculator =new EntropyCalculatorContActions();
    }

    @Test
    void whenLargerSigma_thenLargerEntropy() {

        double e0=calculator.calcEntropy(List.of(0d,0.1));
        double e1=calculator.calcEntropy(List.of(0d,0.5));

        System.out.println("e0 = " + e0);
        System.out.println("e1 = " + e1);

        Assertions.assertTrue(e1>e0);
    }


}
