package policy_gradient_problems.the_problems.cart_pole;

import common.ListUtils;
import lombok.Builder;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExperienceListInfoPole {

    @Builder
    public record ResultManySteps(
            Double sumRewards,
            Optional<StatePole> stateFuture,
            boolean isEndOutside
    ) { }

    List<ExperiencePole> experienceList;
    TrainerParameters parametersTrainer;

    public ExperienceListInfoPole(List<ExperiencePole> experienceList, TrainerParameters parametersTrainer) {
        this.experienceList = experienceList;
        this.parametersTrainer = parametersTrainer;
    }


    public ResultManySteps getResult(int tStart) {
        int tEnd=tStart+parametersTrainer.stepHorizon();
        List<Double> rewardList=new ArrayList<>();
        for (int t = 0; t < Math.min(tEnd,experienceList.size())-1; t++) {
            rewardList.add(experienceList.get(t).reward());
        }
        double rewardSumDiscounted= ListUtils.discountedSum(rewardList,parametersTrainer.gamma());
        boolean isEndOutSide=tEnd>experienceList.size();
        Optional<StatePole> stateFuture=isEndOutSide?Optional.empty(): Optional.of(experienceList.get(tEnd).state());

        return ResultManySteps.builder()
                .sumRewards(rewardSumDiscounted)
                .stateFuture(stateFuture)
                .isEndOutside(isEndOutSide)
                .build();

    }
}
