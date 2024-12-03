package policy_gradient_problems.helpers;

import common.other.Conditionals;
import common.list_arrays.ListUtils;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.ResultManySteps;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/***
 *
 * Used by trainers to derive return (sum rewards) at tStart for given experienceList
 *
 *  n-step return si Gk:k+n=R(k)...gammas^(n-1)*R(k+n-1)+gammas^n*V(S(k+n-1))
 *  k is referring to experience index
 *  therefore
 *  Gk=(k=0,1=2, gammas=1)=R0+V(stateNew(0))   (standard TD)
 *  Gk=(k=0,n=2, gammas=1)=R0+R1+V(stateNew(1))
 */

@AllArgsConstructor
@Log
public class MultiStepReturnEvaluator<V> {
    TrainerParameters parameters;
    List<Experience<V>> experienceList;

    public ResultManySteps<V> getResultManySteps(int tStart) {
        int sizeExpList = experienceList.size();
        throwIfBadArgument(tStart, sizeExpList);
        int idxEndExperience = tStart + parameters.stepHorizon();
        List<Double> rewardList = IntStream.range(tStart, Math.min(idxEndExperience, sizeExpList))  //range -> end exclusive
                .mapToObj(t -> experienceList.get(t).reward())
                .toList();
        double rewardSumDiscounted = ListUtils.discountedSum(rewardList, parameters.gamma());
        boolean isEndOutSide = idxEndExperience > sizeExpList;
        Conditionals.executeIfTrue(isEndOutSide, () ->
                log.fine("Index end experience is outside, idxEndExperience="+idxEndExperience+", sizeExpList= "+sizeExpList));
        StateI<V> stateFuture = isEndOutSide ? null : experienceList.get(idxEndExperience - 1).stateNext();
        boolean isFutureTerminal= stateFuture == null || experienceList.get(idxEndExperience - 1).isTerminal();
        return ResultManySteps.<V>builder()
                .sumRewardsNSteps(rewardSumDiscounted)
                .stateFuture(stateFuture)
                .isFutureStateOutside(isEndOutSide)
                .isFutureTerminal(isFutureTerminal)
                .build();
    }

    public Experience<V> getExperience(int t) {
        int sizeExpList = experienceList.size();
        throwIfBadArgument(t, sizeExpList);
        return experienceList.get(t);
    }


    public Optional<Experience<V>> getEndExperience() {
        int sizeExpList = experienceList.size();
        return (experienceList.isEmpty())
                ? Optional.empty()
                : Optional.of(experienceList.get(sizeExpList - 1));
    }


    public boolean isEndExperienceFail() {
        var expEnd = getEndExperience();
//      return expEnd.isPresent() && (expEnd.orElseThrow().isFail() || expEnd.orElseThrow().isTerminal());
        return expEnd.isPresent() && expEnd.orElseThrow().isFail();


    }

    private static void throwIfBadArgument(int tStart, int sizeExpList) {
        if (tStart > sizeExpList - 1) {
            throw new IllegalArgumentException("Non valid start index, tStart=" + tStart);
        }
    }
}
