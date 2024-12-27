package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.helpers.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;

@AllArgsConstructor
@Getter
@Log
public class TrainerLunarSingleStep implements TrainerI {

    TrainerDependencies dependencies;
    RecorderTrainingProgress recorder;
    MemoryUpdaterSingleStepTrainer memoryUpdater;

    public static TrainerLunarSingleStep of(TrainerDependencies dependencies) {
        return new TrainerLunarSingleStep(
                dependencies,
                RecorderTrainingProgress.empty(),
                MemoryUpdaterSingleStepTrainer.of(dependencies));
    }

    @Override
    public void train() {
        var epCreator = EpisodeCreator.of(dependencies);
        var pmCreator= ProgressMeasuresCreator.of(dependencies);
        recorder.clear();
        log.info("starting training");
        for (int i = 0; i < dependencies.getnEpisodes(); i++) {
            var experiences = epCreator.experiences();
            memoryUpdater.fit(experiences);
            recorder.add(pmCreator.progressMeasures(experiences,null));
        }
    }
}
