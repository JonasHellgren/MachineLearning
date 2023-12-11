package policy_gradient_problems.the_problems.cart_pole;

import common.ListUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import policy_gradient_problems.common_value_classes.TrainerParameters;

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
public class NStepReturnInfoPole {

    @Builder
    public record ResultManySteps(
            Double sumRewards,
            Optional<StatePole> stateFuture,
            boolean isEndOutside
    ) { }

    final List<ExperiencePole> experienceList;
    TrainerParameters parametersTrainer;

    public ResultManySteps getResult(int tStart) {
        int sizeExpList = experienceList.size();
        throwIfBadArgument(tStart, sizeExpList);
        int tEnd=tStart+parametersTrainer.stepHorizon();
        List<Double> rewardList = IntStream.range(tStart, Math.min(tEnd, sizeExpList))  //range -> end exclusive
                .mapToObj(t -> experienceList.get(t).reward())
                .toList();
        double rewardSumDiscounted= ListUtils.discountedSum(rewardList,parametersTrainer.gamma());
        boolean isEndOutSide=tEnd>sizeExpList;
        Optional<StatePole> stateFuture=isEndOutSide
                ? Optional.empty()
                : Optional.of(experienceList.get(tEnd-1).stateNext());

        return ResultManySteps.builder()
                .sumRewards(rewardSumDiscounted)
                .stateFuture(stateFuture)
                .isEndOutside(isEndOutSide)
                .build();

    }

    private static void throwIfBadArgument(int tStart, int sizeExpList) {
        if (tStart > sizeExpList -1) {
            throw new IllegalArgumentException("Non valid start index, tStart="+ tStart);
        }
    }
}
