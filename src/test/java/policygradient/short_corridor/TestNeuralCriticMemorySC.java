package policygradient.short_corridor;

import common.RandUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.the_problems.short_corridor.NeuralCriticMemorySC;
import policy_gradient_problems.the_problems.short_corridor.StateSC;
import java.util.List;
import java.util.Map;

public class TestNeuralCriticMemorySC {

    public static final double VAL0 = -0.5, VAL1=0.5, VAL2=VAL0;
    public static final double TOL = 0.2;
    public static final int NOF_FITS_PER_EPOCH = 1;
    public static final int NOF_CASES = 3;
    static NeuralCriticMemorySC critic;

    @BeforeAll
    public static  void init() {
        critic=NeuralCriticMemorySC.newDefault();
        trainCritic();
        printStateProb();
    }

    @Test
    public void whenObsState0_thenCorrectValue() {
        double value=critic.getOutValue(StateSC.newFromPos(0));
        Assertions.assertEquals(VAL0,value, TOL);
    }

    @Test
    public void whenObsState1_thenCorrectValue() {
        double value=critic.getOutValue(StateSC.newFromPos(1));
        Assertions.assertEquals(VAL1,value, TOL);
    }


    @Test
    public void whenObsState2_thenCorrectValue() {
        double value=critic.getOutValue(StateSC.newFromPos(2));
        Assertions.assertEquals(VAL2,value, TOL);
    }


    static void trainCritic() {
        var caseSGtMap = getCaseSAGtMap();
        for (int ei = 0; ei < 1000 ; ei++) {
            int caseNr= RandUtils.getRandomIntNumber(0, NOF_CASES);
            var pair=caseSGtMap.get(caseNr);
            var state= StateSC.newFromPos(pair.getLeft());
            List<Double> in = state.asList();
            List<Double> out = List.of(pair.getRight());
            critic.fit(List.of(in), out, NOF_FITS_PER_EPOCH);
        }
    }

    private static void printStateProb() {
        Map<Integer, Pair<Integer, Double>> caseSAGtMap = getCaseSAGtMap();

        for (int ci = 0; ci < NOF_CASES ; ci++) {
            var pair=caseSAGtMap.get(ci);
            var estOut=critic.getOutValue(StateSC.newFromPos(pair.getLeft()));
            System.out.println("ci = " + ci);
            System.out.println("inData = " + pair);
            System.out.println("estOut = " + estOut);
        }
    }

    private static Map<Integer, Pair<Integer, Double>> getCaseSAGtMap() {  //case -> pos, val
        return Map.of(
                0,Pair.of(0,VAL0),
                1,Pair.of(1,VAL1),
                2,Pair.of(2,VAL2)

        );
    }


}
