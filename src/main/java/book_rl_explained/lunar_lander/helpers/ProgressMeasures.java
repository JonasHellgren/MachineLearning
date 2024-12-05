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
        Double stdActor) {


    public static ProgressMeasures of(List<ExperienceLunar> experiences,
                                      List<Double> tdList,
                                      List<Double> logStdList) {
        var info=ExperiencesInfo.of(experiences);
        var stdList=logStdList.stream().map(logS -> Math.exp(logS)).toList();

        return ProgressMeasures.builder()
                .sumRewards(info.sumRewards())
                .nSteps(info.nSteps())
                .tdError(tdList.stream().mapToDouble(i-> i).sum())
                .stdActor(MyListUtils.findAverage(stdList).orElseThrow())
                .build();

    }
}
