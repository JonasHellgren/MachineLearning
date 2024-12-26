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
        Double tdErrorClipped,
        Double gradMean,
        Double gradMeanClipped,
        Double stdActor,
        Double stateValuePos2Spd0,
        Double stateValuePos5Spd2) {


    public static ProgressMeasures of(List<ExperienceLunar> experiences,
                                      List<Double> tdList,
                                      List<Double> tdListClipped,
                                      List<Double> stdList,
                                      List<Double> gradMeanList,
                                      List<Double> gradMeanListClipped) {
        var info=ExperiencesInfo.of(experiences);

        return ProgressMeasures.builder()
                .sumRewards(info.sumRewards())
                .nSteps(info.nSteps())
                .tdError(MyListUtils.findAverage(tdList).orElseThrow())
                .tdErrorClipped(MyListUtils.findAverage(tdListClipped).orElseThrow())
                .gradMean(MyListUtils.findAverage(gradMeanList).orElseThrow())
                .gradMeanClipped(MyListUtils.findAverage(gradMeanListClipped).orElseThrow())
                .stdActor(MyListUtils.findAverage(stdList).orElseThrow())
                .build();

    }
}
