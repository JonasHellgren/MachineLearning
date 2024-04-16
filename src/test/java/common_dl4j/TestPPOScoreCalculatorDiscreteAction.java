package common_dl4j;

import common.dl4j.PPOScoreCalculatorDiscreteAction;
import common.other.Conditionals;
import common.list_arrays.ListUtils;
import common.math.MathUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import java.util.List;
import static common.dl4j.Dl4JUtil.createListWithOneHotWithValue;

public class TestPPOScoreCalculatorDiscreteAction {

    public static final double EPSILON = 0.2;
    public static final double TOL = 0.01;
    public static final double EPS = 1e-2;
    PPOScoreCalculatorDiscreteAction scoreCalculator;

    @BeforeEach
    void init() {
        scoreCalculator=new PPOScoreCalculatorDiscreteAction(EPSILON);
    }

    @Test
    void whenLabelsAndProbs_thenCorrect() {
        int a = 2;
        double adv = 1.0;
        double pOld = 0.5;
        double pNew2 = 1.0;

        INDArray label = Nd4j.create(new double[]{a, adv, pOld}); // Action 2, advantage 1.0, old probability 0.5
        INDArray estProbabilities = Nd4j.create(new double[]{0, 0, pNew2}); // Extremely high new probability for action 2
        double lb=adv*(1-EPSILON);
        double ub=adv*(1+EPSILON);
        double advTimesProbRatio=adv*pNew2/pOld;
        double score= scoreCalculator.calcScore(label,estProbabilities);
        printing(lb, ub, advTimesProbRatio, score);
        boolean isClipped= MathUtils.isEqualDoubles(score,lb, TOL) || MathUtils.isEqualDoubles(score,ub, TOL);

        Assertions.assertTrue(score>=lb && score<=ub);
        Conditionals.executeIfTrue(!isClipped,() -> Assertions.assertEquals(advTimesProbRatio,score));
    }


    @ParameterizedTest
    @CsvSource({"2,1.0,0.9,1.0",   //a,adv,pOld,pNew
            "2,1.0,0.5,1.0",
            "2,-1.0,0.5,1.0",
            "2,10.0,0.1,1.0",
            "2,-10.0,0.01,1.0"})
    void whenLabel_thenClipping(ArgumentsAccessor arguments) {
        double a= arguments.getDouble(0);
        double adv= arguments.getDouble(1);
        double pOld= arguments.getDouble(2);
        double pNew= arguments.getDouble(3);

        INDArray label = Nd4j.create(new double[]{a, adv, pOld}); // Action 2, advantage 1.0, old probability 0.5
        var oneHot=createListWithOneHotWithValue(3, (int) a,pNew);
        INDArray estProbabilities = getIndArray(oneHot);
        var minMaxList=List.of(adv*(1-EPSILON),adv*(1+EPSILON));
        double lb= ListUtils.findMin(minMaxList).orElseThrow();
        double ub=ListUtils.findMax(minMaxList).orElseThrow();
        double advTimesProbRatio=adv*pNew/pOld;
        double score= scoreCalculator.calcScore(label,estProbabilities);
        printing(lb, ub, advTimesProbRatio, score);
        boolean isClipped= MathUtils.isEqualDoubles(score,lb, TOL) || MathUtils.isEqualDoubles(score,ub, TOL);

        Assertions.assertTrue(score>=lb && score<=ub);
        Conditionals.executeIfTrue(!isClipped,() -> Assertions.assertEquals(advTimesProbRatio,score));
    }

    private static INDArray getIndArray(List<Double> oneHot) {
        return Nd4j.create(oneHot.stream()
                .mapToDouble(d -> d == 0.0 ? 0.0 : d).toArray()).reshape(oneHot.size());
    }


    private static void printing(double lb, double ub, double advTimesProbRatio, double score) {
        System.out.println("lb = " + lb+", ub = " + ub+", advTimesProbRatio = " + advTimesProbRatio+", score = " + score);
    }


}
