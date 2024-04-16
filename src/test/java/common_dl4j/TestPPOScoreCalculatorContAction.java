package common_dl4j;

import common.dl4j.PPOScoreCalculatorContAction;
import common.dl4j.PPOScoreCalculatorI;
import common.math.MathUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class TestPPOScoreCalculatorContAction {
    public static final double EPSILON = 0.2;

    PPOScoreCalculatorI calculator;

    @BeforeEach
    void init() {
        calculator = new PPOScoreCalculatorContAction(EPSILON);
    }

    /**
     * The important logic in tests below is that when a is similar to m and adv is high, we are pushing m and s
     * to increase prob sampling a again
     */

    @ParameterizedTest  //m,a,adv,dm,ds,isScoreIncreasing
    @CsvSource({"1,2,1.0,0.02,0.0,true",     //action 2 is good (larger than m), hence pos dm gives increasing score
            "1,2,1.0,0.0,1e-3,true",     //a decrease in s will increase the PDF value at a if and only if a is close to m
            "1,2,1.0,-0.1,0.0,false",  //action 2 is good (larger than m), hence neg dm gives decreasing score
            "1,0.5,1.0,-0.1,0.0,true", //action 0.5 is good (smaller than m), hence neg dm gives increasing score
            "1,2,-1.0,-0.1,0.0,true", //action 2 is bad (larger than m), hence neg dm gives increasing score
            "1,1.01,-1.0,0.0,1e-3,true", //close to m but bad, increasing ds gives increasing score
            "1,1.1,1.0,0.0,-1e-3,true", //a decrease in s will increase the PDF value at a if and only if a is close to m (close and good)
            "1,2,1.0,0.0,1e-3,true",  //an increase in s will decrease the PDF value at a, if a is far from m, try more if not close
            "1,0.5,-1.0,+1e-2,+1e-3,true",  //tried action smaller than m, not good, better adjust back
            "1,2.0,-1.0,-1e-2,+1e-3,true",  //tried action larger than m, not good, better adjust back
            "0.38178,0.2848,1.0,-1e-2,0.0,true",  //m larger than a, reducing m gives larger score
            "1,2,0.001,0.1,0.0,true"})
    //action 2 is neutral (small pos adv), hence pos dm gives increasing score
    void givenChangeInNetOutput_thenCorrectScore(ArgumentsAccessor arguments) {


        //double m = 1, s = .5;  //old net output
        //double m = 0.38178,
        double s = .6182;  //old net output

        double m = arguments.getDouble(0);
        double a = arguments.getDouble(1);
        double adv = arguments.getDouble(2);
        double dm = arguments.getDouble(3);
        double ds = arguments.getDouble(4);
        boolean isScoreIncreasing = arguments.getBoolean(5);

        double pOld = MathUtils.pdf(a, m, s);
        INDArray label = Nd4j.create(new double[]{a, adv, pOld});
        INDArray netOutPut0 = Nd4j.create(new double[]{m, s});
        INDArray netOutPut = Nd4j.create(new double[]{m + dm, s + ds});
        double score0 = calculator.calcScore(label, netOutPut0);
        double score = calculator.calcScore(label, netOutPut);

        System.out.println("score0 = " + score0 + ", score = " + score + ", larger=" + (score > score0));

        Assertions.assertEquals(score > score0, isScoreIncreasing);
    }


}
