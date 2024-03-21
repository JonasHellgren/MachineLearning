package policy_gradient_problems.domain.common_episode_trainers;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Triple;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.MultiStepResults;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.helpers.NStepReturnInfo;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static common.Conditionals.executeIfTrue;

/***
 * The function criticOut and consumer fitCritic is used to make the class generic,
 * would not be fully generic if for example AgentParamActorNeuralCriticI<V> agent is used instead

 *  An episode gives a set of experiences: e0, e1, e2, ei,.....,e_i+n
 *  Two cases are present for e_end: 1) it is fail state 2) not fail state
 *  In the first case the actor should learn from the entire episode, it shall learn from the mistake resulting in fail
 *  But in the second case it shall stop learning after e_end-n. The reason is that the experiences after end-n are
 *  "miss leading". They indicate few rewards remaining, but that is not due to failure but due to non-fail terminal
 *  episode. Many environments stop after a specific amount of steps.

 */

@Log
@AllArgsConstructor
public class MultiStepNeuralCriticUpdater<V> {

    TrainerParameters parameters;
    Function<StateI<V>,Double> criticOut;
    Consumer<Triple<List<List<Double>>,List<Double>,Integer>> fitCritic;  //todo MultiStepResults

    //todo split method below

    public MultiStepResults updateCritic(List<Experience<V>> experiences) {
        var results = getMultiStepResults(experiences);
        executeIfTrue(!results.stateValuesList().isEmpty(), () ->
                fitCritic.accept(Triple.of(results.stateValuesList(), results.valueTarList(), results.nofSteps())));
        executeIfTrue(results.stateValuesList().isEmpty(), () -> log.warning("empty stateValuesList"));
        return results;
    }

    private  MultiStepResults getMultiStepResults(List<Experience<V>> experiences) {
        Integer n = parameters.stepHorizon();
        int nofExperiences = experiences.size();
        double gammaPowN = Math.pow(parameters.gamma(), n);
        var elInfo = new NStepReturnInfo<>(experiences, parameters);
        int tEnd = elInfo.isEndExperienceFail() ? nofExperiences : nofExperiences - n + 1;  //explained in top of file

        var results=MultiStepResults.create(tEnd);
        IntStream.range(0, tEnd).forEach(t -> {
            var resMS = elInfo.getResultManySteps(t);
            double sumRewards = resMS.sumRewardsNSteps;
            double valueFut = !resMS.isFutureStateOutside
                    ? gammaPowN * criticOut.apply(resMS.stateFuture)
                    : 0;
            results.addStateValues(elInfo.getExperience(t).state().asList());
            results.addAction(elInfo.getExperience(t).action());
            results.addValue(criticOut.apply(elInfo.getExperience(t).state()));
            results.addValueTarget(sumRewards + valueFut);
        });
        return results;
    }

}
