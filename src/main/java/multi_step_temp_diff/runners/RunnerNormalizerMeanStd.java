package multi_step_temp_diff.runners;

import common.ListUtils;
import multi_step_temp_diff.domain.agent_parts.neural_memory.normalizer.NormalizerInterface;
import multi_step_temp_diff.domain.agent_parts.neural_memory.normalizer.NormalizerMeanStd;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.lang.System.out;
import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentParameters.*;

/**
 * Tha largest alpha giving true in alphaBoolMap should be adequate
 */

public class RunnerNormalizerMeanStd {

    public static final List<Double> ALPHA_LIST = List.of(0d, 1d, 3d, 5d, 10d, 20d);
    static NormalizerInterface normalizer;
    static ChargeEnvironmentSettings envSettings;


    public static void main(String[] args) {

        envSettings = ChargeEnvironmentSettings.newDefault();
        double rewardBad = envSettings.rewardBad();
        Map<Double, List<Double>> alphaListOfValuesMap = getAlphaListMap(rewardBad);

        Triple<Double, Double, Double> valuesToNormalize = Triple.of(rewardBad, -envSettings.costQue(), 0d);
        Map<Double, Triple<Double, Double, Double>> alphaNormValuesMap =
                getAlphaNormValuesMap(alphaListOfValuesMap, valuesToNormalize);
        alphaNormValuesMap.entrySet().forEach(out::println);

        Map<Double, Boolean> alphaBoolMap = getAlphaBooleanMap(alphaNormValuesMap);
        alphaBoolMap.entrySet().forEach(out::println);

    }

    @NotNull
    private static Map<Double, Boolean> getAlphaBooleanMap(
            Map<Double, Triple<Double,
                    Double, Double>> alphaNormValuesMap) {
        Predicate<Triple<Double, Double, Double>> isTripleOk = (t) ->
                normalizer.isNormalizedValueOk(t.getLeft()) &&
                        normalizer.isNormalizedValueOk(t.getMiddle()) &&
                        normalizer.isNormalizedValueOk(t.getRight());
        List<Boolean> booleans = alphaNormValuesMap.keySet().stream()
                .map(a -> isTripleOk.test(alphaNormValuesMap.get(a))).toList();

        return alphaNormValuesMap.keySet().stream()
                .map(a -> Pair.of(a, isTripleOk.test(alphaNormValuesMap.get(a))))
                .collect(Collectors.toMap(
                        p -> p.getLeft(), p -> p.getRight()));
    }

    @NotNull
    private static Map<Double, Triple<Double, Double, Double>> getAlphaNormValuesMap(
            Map<Double, List<Double>> alphaListOfValuesMap,
            Triple<Double, Double, Double> valuesToNormalize) {
        Map<Double, Triple<Double, Double, Double>> alphaNormValuesMap = new HashMap<>();
        for (double alpha : ALPHA_LIST) {
            normalizer = new NormalizerMeanStd(alphaListOfValuesMap.get(alpha));
            alphaNormValuesMap.put(alpha,
                    Triple.of(normalizer.normalize(valuesToNormalize.getLeft()),
                            normalizer.normalize(valuesToNormalize.getMiddle()),
                            normalizer.normalize(valuesToNormalize.getRight())));
        }
        return alphaNormValuesMap;
    }

    @NotNull
    private static Map<Double, List<Double>> getAlphaListMap(double rewardBad) {
        Map<Double, List<Double>> alphaListOfValuesMap = new HashMap<>();
        for (double alpha : ALPHA_LIST) {
            alphaListOfValuesMap.put(alpha, ListUtils.merge(List.of(rewardBad * alpha),
                    CHARGE_REWARD_VALUES_EXCEPT_FAIL));
        }
        return alphaListOfValuesMap;
    }

}
