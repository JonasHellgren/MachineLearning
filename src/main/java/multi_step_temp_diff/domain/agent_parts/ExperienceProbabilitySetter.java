package multi_step_temp_diff.domain.agent_parts;

import common.MathUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.List;
import java.util.function.BiFunction;

@AllArgsConstructor
public class ExperienceProbabilitySetter<S> {

    List<NstepExperience<S>> buffer;
    @NonNull Double alpha;

    static BiFunction<Double,Double,Double> prioRaised =(p, alpha) -> Math.pow(p,alpha);

    public void setProbabilities() {
        for (NstepExperience<S> experience:buffer) {
            experience.probability = prioRaised.apply(experience.prioritization,alpha);
        }
        double sumTerms = getSumTerms();
        divideEveryProbabilityWithSumOfProbabilities(sumTerms);
    }

    private void divideEveryProbabilityWithSumOfProbabilities(double sumTerms) {
        for (NstepExperience<S> experience:buffer) {
            experience.probability = experience.probability/ sumTerms;
        }
    }

    private double getSumTerms() {
        double sumTerms=buffer.stream().mapToDouble(e -> prioRaised.apply(e.probability,alpha)).sum();
        if (MathUtils.isZero(sumTerms)) {
            throw new RuntimeException("All probabilities are zero");
        }
        return sumTerms;
    }


}
