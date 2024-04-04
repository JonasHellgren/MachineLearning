package policygradient.short_corridor;

import common.ListUtils;
import common.RandUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.environments.short_corridor.NeuralActorMemorySCCEMLoss;
import policy_gradient_problems.environments.short_corridor.StateSC;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Probabilities converges badly for negative Gt, tries to change more despite prob already is 0 or 1.
 */

public class TestNeuralActorMemorySCCEMLoss {

    static final int N_ACTIONS = 2;
    public static final int NOF_STEPS = 1_000;
    static NeuralActorMemorySCCEMLoss actor;

    @BeforeAll
    public static void init() {
        actor= NeuralActorMemorySCCEMLoss.newDefault();
        printStateProb();
    }

    @Test
    @Disabled("Long time")
    void whenObsState0_thenHigherProbA1() {
        trainActor();
        printStateProb();
        var probs=actor.getOutValue(ListUtils.toArray(List.of(0d)));
        Assertions.assertTrue(probs[1]>probs[0]);
    }


    @Test
    @Disabled("Long time")
    void whenObsState2_thenHigherProbA0() {
        trainActor();
        printStateProb();
        var probs=actor.getOutValue(ListUtils.toArray(List.of(2d)));
        Assertions.assertTrue(probs[0]>probs[1]);
    }

    static void trainActor() {
        Map<Integer, Triple<Integer, Integer, Double>> caseSAGtMap = getCaseSAGtMap();
        for (int ei = 0; ei < NOF_STEPS; ei++) {
            int caseNr= RandUtils.getRandomIntNumber(0,4);
            var triplet=caseSAGtMap.get(caseNr);
            var state=StateSC.newFromRealPos(triplet.getLeft());
            List<Double> in = state.asList();
            List<Double> onHotOut = createOut(triplet.getMiddle(), triplet.getRight());
            actor.fit(List.of(in), List.of(onHotOut));
        }
    }

    private static void printStateProb() {
        Map<Integer, Triple<Integer, Integer, Double>> caseSAGtMap = getCaseSAGtMap();

        for (int ci = 0; ci < 4 ; ci=ci+2) {
            var triplet=caseSAGtMap.get(ci);
            double[] inData = {triplet.getLeft()};
            var probs=actor.getOutValue(inData);
            System.out.println("inData = " + Arrays.toString(inData));
            System.out.println("probs = " + Arrays.toString(probs));
        }
    }

    //(0):s0,a0 -> Gt=-1; (1):s0,a1 -> Gt=1; (2):2,a0 -> Gt=1, (3):s2,a1 -> Gt=-1
    private static Map<Integer, Triple<Integer, Integer, Double>> getCaseSAGtMap() {
        return Map.of(
                0,Triple.of(0,0,-1d), 1,Triple.of(0,1,1d),
             //   0,Triple.of(0,0,0d), 1,Triple.of(0,1,1d),  //give smoother gradients

                2,Triple.of(2,0,1d),  3,Triple.of(2,1,-1d)
             //   2,Triple.of(2,0,1d),  3,Triple.of(2,1,0d)  //give smoother gradients
        );
    }

    private static List<Double> createOut(int action, double value) {
        List<Double> out= ListUtils.createListWithEqualElementValues(N_ACTIONS,0d);
        out.set(action, value);
        return out;
    }


}
