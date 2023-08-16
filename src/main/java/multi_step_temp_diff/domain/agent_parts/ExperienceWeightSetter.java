package multi_step_temp_diff.domain.agent_parts;

import com.codepoetics.protonpack.functions.TriFunction;
import common.Conditionals;
import common.MathUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.util.List;

/***
 * Details in https://paperswithcode.com/method/prioritized-experience-replay
 */

@AllArgsConstructor
@Log
public class ExperienceWeightSetter<S> {

    @NonNull List<NstepExperience<S>> buffer;
    @NonNull Double beta;

    public static final double DUMMY_WEIGHT = 1;
    static TriFunction<Double, Double, Double, Double> weightFunction = (oneDivN, p, beta) ->
            (MathUtils.isZero(p)) ? DUMMY_WEIGHT : Math.pow(oneDivN * 1 / p, beta);

    public void setWeights() {
        double oneDivN = (double) 1 / buffer.size();
        buffer.forEach(e -> e.weight = weightFunction.apply(oneDivN, e.probability, beta));
        double maxWeight = buffer.stream().mapToDouble(e -> e.weight).max().orElseThrow();
        Conditionals.executeOneOfTwo(MathUtils.isZero(maxWeight),
                () -> log.warning("maxWeight is zero"),
                () -> divideEveryWeightWithMaxWeight(maxWeight));
    }

    private void divideEveryWeightWithMaxWeight(double maxWeight) {
        for (NstepExperience<S> experience : buffer) {
            experience.weight = experience.weight / maxWeight;
        }
    }

}
