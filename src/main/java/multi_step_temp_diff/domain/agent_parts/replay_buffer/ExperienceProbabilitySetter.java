package multi_step_temp_diff.domain.agent_parts.replay_buffer;

import common.MathUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.util.List;
import java.util.function.BiFunction;


/*** Described more in detail in for example
 * https://paperswithcode.com/method/prioritized-experience-replay
 */

@AllArgsConstructor
@Log
public class ExperienceProbabilitySetter<S> {

    @NonNull List<NstepExperience<S>> buffer;
    @NonNull Double alpha;

    static BiFunction<Double,Double,Double> prioRaised =(p, alpha) -> Math.pow(p,alpha);

    public void setProbabilities() {
        buffer.forEach(e -> e.probability= prioRaised.apply(e.prioritization,alpha));
        double sumTerms = getSumTerms();
        if (MathUtils.isZero(sumTerms)) {
            log.warning("All probabilities are zero, setting uniform probabilities");
            buffer.forEach(e -> e.probability= (double) (1/ buffer.size()));
        } else {
            divideEveryProbabilityWithSumOfProbabilities(sumTerms);
        }
    }

    private void divideEveryProbabilityWithSumOfProbabilities(double sumTerms) {
        double oneDivSumTerms=1 / sumTerms;
        for (NstepExperience<S> experience:buffer) {
            experience.probability = experience.probability*oneDivSumTerms;
        }
    }

    public double getSumTerms() {
        return buffer.stream().mapToDouble(e -> e.probability).sum();
    }

}
