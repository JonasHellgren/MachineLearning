package multi_agent_rl.helpers;

import com.google.common.base.Preconditions;
import common.list_arrays.ListUtils;
import common.other.Conditionals;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import multi_agent_rl.domain.abstract_classes.StateI;
import multi_agent_rl.domain.value_classes.Experience;
import multi_agent_rl.domain.value_classes.SingleResultMultiStepper;
import multi_agent_rl.domain.value_classes.TrainerParameters;

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
 *
 *  A basic principle is that reward for stepping into terminal is included but value
 *  of terminal stateNew is zero
 */

@AllArgsConstructor
@NoArgsConstructor
@Log
public class MultiStepReturnEvaluator<V,O> {
    TrainerParameters parameters;
    List<Experience<V,O>> experiences;

    public void setParametersAndExperiences(TrainerParameters parameters,
                                            List<Experience<V,O>> experiences) {
        this.parameters = parameters;
        this.experiences=experiences;
    }

    public void setExperiences(List<Experience<V,O>> experiences) {
        this.experiences = experiences;
    }

    public SingleResultMultiStepper<V,O> evaluate(int tStart) {
        var informer=new EpisodeInfo<>(experiences);
        int nExperiences = informer.size();
        Preconditions.checkArgument(tStart < nExperiences,"Non valid start index, tStart=" + tStart);
        int idxEnd = tStart + parameters.stepHorizon();
        var rewards = IntStream.rangeClosed(tStart, Math.min(idxEnd, nExperiences-1))  //end inclusive
                .mapToObj(t -> informer.experienceAtTime(t).reward()).toList();
        double rewardSumDiscounted = ListUtils.discountedSum(rewards, parameters.gamma());
        boolean isEndOutSide = idxEnd > nExperiences-1;
        maybeLog(nExperiences, idxEnd, isEndOutSide);
        StateI<V,O> stateFuture = isEndOutSide
                ? null
                : informer.experienceAtTime(idxEnd - 1).stateNew();
        boolean isFutureTerminal= !isEndOutSide && informer.experienceAtTime(idxEnd).isTerminal();
        return SingleResultMultiStepper.<V,O>builder()
                .sumRewardsNSteps(rewardSumDiscounted)
                .stateFuture(stateFuture)
                .isFutureStateOutside(isEndOutSide)
                .isFutureTerminal(isFutureTerminal)
                .build();
    }

    private static void maybeLog(int nExperiences, int idxEndExperience, boolean isEndOutSide) {
        Conditionals.executeIfTrue(isEndOutSide, () ->
                log.fine("Index end experience is outside, idxEndExperience="+
                        idxEndExperience +", nExperiences= "+ nExperiences));
    }

}
