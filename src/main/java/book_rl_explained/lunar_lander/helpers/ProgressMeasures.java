package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.trainer.ExperienceLunar;
import lombok.Builder;
import lombok.With;
import org.hellgren.utilities.list_arrays.ListCreator;
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

    public record MeasureLists(List<Double> tdList,
                               List<Double> tdListClipped,
                               List<Double> stdList,
                               List<Double> gradMeanList,
                               List<Double> gradMeanListClipped) {

        public static MeasureLists empty() {
            return new MeasureLists(
                    ListCreator.emptyDouble(),
                    ListCreator.emptyDouble(),
                    ListCreator.emptyDouble(),
                    ListCreator.emptyDouble(),
                    ListCreator.emptyDouble());
        }
        public void addTd(double td) {
            tdList.add(td);}

        public void addTdClipped(double td) {
            tdListClipped.add(td); }

        public void addStd(double std) {
            stdList.add(std); }

        public void addGradMean(double gradMean) {
            gradMeanList.add(gradMean); }

        public void addGradMeanClipped(double gradMean) {
            gradMeanListClipped.add(gradMean); }


    }

    public static ProgressMeasures of(List<ExperienceLunar> experiences,
                                    MeasureLists measureLists) {
        var info=ExperiencesInfo.of(experiences);

        return ProgressMeasures.builder()
                .sumRewards(info.sumRewards())
                .nSteps(info.nSteps())
                .tdError(MyListUtils.findAverage(measureLists.tdList).orElseThrow())
                .tdErrorClipped(MyListUtils.findAverage(measureLists.tdListClipped).orElseThrow())
                .gradMean(MyListUtils.findAverage(measureLists.gradMeanList).orElseThrow())
                .gradMeanClipped(MyListUtils.findAverage(measureLists.gradMeanListClipped).orElseThrow())
                .stdActor(MyListUtils.findAverage(measureLists.stdList).orElseThrow())
                .build();

    }
}
