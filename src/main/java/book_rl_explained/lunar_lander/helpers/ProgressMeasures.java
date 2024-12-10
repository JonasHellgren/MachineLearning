package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.trainer.ExperienceLunar;
import lombok.Builder;
import lombok.With;
import org.hellgren.utilities.list_arrays.MyListUtils;
import java.util.List;

@With
@Builder
public record ProgressMeasures(
        Double sumRewards,
        Integer nSteps,
        Double tdError,
        Double stdActor,
        Double sumRewardsNoExploring) {


    public static ProgressMeasures of(List<ExperienceLunar> experiences,
                                      List<Double> tdList,
                                      List<Double> stdList) {
        var info=ExperiencesInfo.of(experiences);

        return ProgressMeasures.builder()
                .sumRewards(info.sumRewards())
                .nSteps(info.nSteps())
                .tdError(tdList.stream().mapToDouble(i-> i).sum())
                .stdActor(MyListUtils.findAverage(stdList).orElseThrow())
                .build();

    }
}