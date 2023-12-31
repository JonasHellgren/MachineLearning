package policy_gradient_problems.common_helpers;

import common.ListUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.cart_pole.ExperiencePole;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/***
 *  n-step return si Gk:k+n=R(k)...gamma^(n-1)*R(k+n-1)+gamma^n*V(S(k+n-1))
 *  k is referring to experience index
 *  therefore
 *  Gk=(k=0,1=2, gamma=1)=R0+V(stateNew(0))   (standard TD)
 *  Gk=(k=0,n=2, gamma=1)=R0+R1+V(stateNew(1))
 */

@AllArgsConstructor
public class NStepReturnInfo<V> {

    @Builder
    public static class ResultManySteps<V> {
        public Double sumRewardsNSteps;
        public StateI<V> stateFuture;
        public boolean isEndOutside;
    }

////Following would have been cleaner but gives compilation error
/*    @Builder
    public  record ResultManySteps<V> (
        Double sumRewardsNSteps,
        StateI<V> stateFuture,
        boolean isEndOutside)
    {    }*/

    final List<Experience<V>> experienceList;
    TrainerParameters parametersTrainer;

    public ResultManySteps<V> getResultManySteps(ExperiencePole e) {
        int tStart = experienceList.indexOf(e);
        if (tStart == -1) {
            throw new IllegalArgumentException("Experience not present");
        }
        return getResultManySteps(tStart);
    }

    public ResultManySteps<V> getResultManySteps(int tStart) {
        int sizeExpList = experienceList.size();
        throwIfBadArgument(tStart, sizeExpList);
        int tEnd = tStart + parametersTrainer.stepHorizon();
        List<Double> rewardList = IntStream.range(tStart, Math.min(tEnd, sizeExpList))  //range -> end exclusive
                .mapToObj(t -> experienceList.get(t).reward())
                .toList();
        double rewardSumDiscounted = ListUtils.discountedSum(rewardList, parametersTrainer.gamma());
        boolean isEndOutSide = tEnd > sizeExpList;
/*
        Optional<StateI<V>> stateFuture=isEndOutSide
                ? Optional.empty()
                : Optional.of(experienceList.get(tEnd-1).stateNext());
*/
        StateI<V> stateFuture = isEndOutSide
                ? null
                : experienceList.get(tEnd - 1).stateNext();

        return ResultManySteps.<V>builder()
                .sumRewardsNSteps(rewardSumDiscounted)
                .stateFuture(stateFuture)
                .isEndOutside(isEndOutSide)
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
        return expEnd.isPresent() && expEnd.orElseThrow().isFail();


    }

    private static void throwIfBadArgument(int tStart, int sizeExpList) {
        if (tStart > sizeExpList - 1) {
            throw new IllegalArgumentException("Non valid start index, tStart=" + tStart);
        }
    }
}
