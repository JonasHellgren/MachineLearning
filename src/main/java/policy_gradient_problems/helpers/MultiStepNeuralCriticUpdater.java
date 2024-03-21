package policy_gradient_problems.helpers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.agent_interfaces.NeuralCriticI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.MultiStepResults;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import java.util.List;
import java.util.stream.IntStream;
import static common.Conditionals.executeOneOfTwo;

/**
 * An episode gives a set of experiences: e0, e1, e2, ei,.....,e_i+n
 * Two cases are present for e_end: 1) it is fail state 2) not fail state
 * In the first case the actor should learn from the entire episode, it shall learn from the mistake resulting in fail
 * But in the second case it shall stop learning after e_end-n. The reason is that the experiences after end-n are
 * "miss leading". They indicate few rewards remaining, but that is not due to failure but due to non-fail terminal
 * episode. Many environments stop after a specific amount of steps.
 */

@Log
@AllArgsConstructor
public class MultiStepNeuralCriticUpdater<V> {

    @NonNull TrainerParameters parameters;
    @NonNull NeuralCriticI<V> agent;

    public void updateCritic(MultiStepResults msRes) {
        executeOneOfTwo(!msRes.stateValuesList().isEmpty(),
                () -> agent.fitCritic(msRes.stateValuesList(), msRes.valueTarList()),
                () -> log.warning("empty stateValuesList"));
    }

    public MultiStepResults getMultiStepResults(List<Experience<V>> experiences) {
        Integer n = parameters.stepHorizon();
        int nofExperiences = experiences.size();
        double gammaPowN = Math.pow(parameters.gamma(), n);
        var elInfo = new NStepReturnInfo<>(experiences, parameters);
        int tEnd = elInfo.isEndExperienceFail() ? nofExperiences : nofExperiences - n + 1;  //explained in top of file

        var results = MultiStepResults.create(tEnd);
        IntStream.range(0, tEnd).forEach(t -> {
            var resMS = elInfo.getResultManySteps(t);
            double sumRewards = resMS.sumRewardsNSteps;
            double valueFut = !resMS.isFutureStateOutside
                    ? gammaPowN * agent.getCriticOut(resMS.stateFuture)
                    : 0;
            results.addStateValues(elInfo.getExperience(t).state().asList());
            results.addAction(elInfo.getExperience(t).action());
            results.addValue(agent.getCriticOut(elInfo.getExperience(t).state()));
            results.addValueTarget(sumRewards + valueFut);
        });
        return results;
    }

}
