package safe_rl.helpers;

import com.google.common.base.Preconditions;
import common.list_arrays.ListUtils;
import common.other.Conditionals;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.SingleResultMultiStepper;
import safe_rl.domain.value_classes.TrainerParameters;

import java.util.List;
import java.util.stream.IntStream;

/***
 *
 * Used by trainers to derive return (sum rewards) at tStart for given experienceList
 *
 *  n-step return si Gk:k+n=R(k)...gamma^(n-1)*R(k+n-1)+gamma^n*V(S(k+n-1))
 *  k is referring to experience index
 *  therefore
 *  Gk=(k=0,1=2, gamma=1)=R0+V(stateNew(0))   (standard TD)
 *  Gk=(k=0,n=2, gamma=1)=R0+R1+V(stateNew(1))
 */

@AllArgsConstructor
@Log
public class MultiStepReturnEvaluator<V> {
    TrainerParameters parameters;
    List<Experience<V>> experiences;

    public SingleResultMultiStepper<V> getResultManySteps(int tStart) {
        var informer=new EpisodeInfo<>(experiences);
        int nExperiences = informer.size();
        Preconditions.checkArgument(tStart > nExperiences - 1,"Non valid start index, tStart=" + tStart);
        int idxEndExperience = tStart + parameters.stepHorizon();
        var rewards = IntStream.range(tStart, Math.min(idxEndExperience, nExperiences))  //range -> end exclusive
                .mapToObj(t -> informer.experienceAtTime(t).rewardApplied()).toList();
        double rewardSumDiscounted = ListUtils.discountedSum(rewards, parameters.gamma());
        boolean isEndOutSide = idxEndExperience > nExperiences;
        maybeLog(nExperiences, idxEndExperience, isEndOutSide);
        StateI<V> stateFuture = isEndOutSide ? null : informer.experienceAtTime(idxEndExperience - 1).stateNextApplied();
        boolean isFutureTerminal= stateFuture == null || informer.experienceAtTime(idxEndExperience - 1).isTerminalApplied();
        return SingleResultMultiStepper.<V>builder()
                .sumRewardsNSteps(rewardSumDiscounted)
                .stateFuture(stateFuture)
                .isFutureStateOutside(isEndOutSide)
                .isFutureTerminal(isFutureTerminal)
                .build();
    }

    private static void maybeLog(int nExperiences, int idxEndExperience, boolean isEndOutSide) {
        Conditionals.executeIfTrue(isEndOutSide, () ->
                log.fine("Index end experience is outside, idxEndExperience="+ idxEndExperience +", nExperiences= "+ nExperiences));
    }

}
