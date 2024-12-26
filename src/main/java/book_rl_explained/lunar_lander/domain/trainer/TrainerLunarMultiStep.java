package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.helpers.*;
import book_rl_explained.radial_basis.TrainData;
import common.other.Conditionals;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;
import org.hellgren.utilities.timer.CpuTimer;

import static org.hellgren.utilities.conditionals.Conditionals.executeOneOfTwo;

@AllArgsConstructor
@Getter
@Log
public class TrainerLunarMultiStep implements TrainerI {

    TrainerDependencies dependencies;
    RecorderTrainingProgress recorder;
    MemoryUpdaterMultiStepTrainer memoryUpdater;


    public static TrainerLunarMultiStep of(TrainerDependencies dependencies) {
        return new TrainerLunarMultiStep(dependencies,
                RecorderTrainingProgress.empty(),
                MemoryUpdaterMultiStepTrainer.of(dependencies));
    }

    @Override
    public void train() {
        var epCreator = EpisodeCreator.of(dependencies);
        var pmCreator = ProgressMeasuresCreator.of(dependencies);
        var msrGenerator = MultiStepResultsGenerator.of(dependencies);
        var timer= CpuTimer.empty();
        recorder.clear();
        log.info("starting training");
        for (int i = 0; i < dependencies.getnEpisodes(); i++) {
            var experiences = epCreator.experiences();
            var msr = msrGenerator.generate(experiences);
            executeOneOfTwo(dependencies.trainerParameters().isBatchLearning(),
                    () -> memoryUpdater.fitBatch(msr),
                    () -> memoryUpdater.fitStep(msr));
            recorder.add(pmCreator.progressMeasures(experiences));
        }
        log.info("Training finished in (s): " + timer.timeInSecondsAsString());

    }

}
