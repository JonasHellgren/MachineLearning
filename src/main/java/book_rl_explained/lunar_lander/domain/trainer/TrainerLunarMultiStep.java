package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.helpers.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;
import org.hellgren.utilities.timer.CpuTimer;

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
            var experiences = epCreator.create();
            var msr = msrGenerator.generate(experiences);
            memoryUpdater.fit(msr);
            recorder.add(pmCreator.progressMeasures(experiences,msr));
        }
        log.info("Training finished in (s): " + timer.timeInSecondsAsString());

    }

}
