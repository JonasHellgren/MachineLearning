package common;

import common.math.SafeGradientClipper;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

class TestSafeGradientClipper {

    SafeGradientClipper clipperDoNothing;
    SafeGradientClipper clipper;

     @BeforeEach
    void init() {
         clipperDoNothing = SafeGradientClipper.builder().build();
         clipper = SafeGradientClipper.builder()
                 .valueMin(-10d).valueMax(10d)
                 .gradMin(-2d).gradMax(2d)
                 .build();
     }

     @Test
    void whenDoNothing_thenCorrect() {
         for (int i = 0; i < 100; i++) {
             double grad0= RandomUtils.nextDouble(0, 100)-50;
             Assertions.assertEquals(grad0,clipperDoNothing.modify(grad0,0));
         }
     }

    @ParameterizedTest
    @CsvSource({
            "1,1  ,1",  //grad0, value, gradClipped
            "4,1  ,2",   //gradMax restricts
            "2,9  ,1",   //valueMax restricts
            "2,19  ,0",  //above valueMax
            "-3,9  ,-2",  //gradMin restricts
            "-2,-9  ,-1",  //valueMin restricts
            "-22,5  ,-2"})  //gradMin restricts
    void whenSum_thenCorrect1(ArgumentsAccessor arguments) {
        double grad0  = arguments.getDouble(0);
        double value = arguments.getDouble(1);
        double gradClippedExpected = arguments.getDouble(2);
        Assertions.assertEquals(gradClippedExpected,clipper.modify(grad0,value));
    }

}
