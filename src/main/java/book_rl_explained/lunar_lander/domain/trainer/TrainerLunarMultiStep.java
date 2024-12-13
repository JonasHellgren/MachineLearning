package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.helpers.EpisodeCreator;
import book_rl_explained.lunar_lander.helpers.RecorderTrainingProgress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;

@AllArgsConstructor
@Getter
@Log
public class TrainerLunarMultiStep implements TrainerI {
    public static final double VALUE_TERM = 0d;

    TrainerDependencies dependencies;
    RecorderTrainingProgress recorder;


    @Override
    public void train() {
        var creator = new EpisodeCreator(dependencies);
        var msrGenerator = MultiStepResultsGenerator.of(dependencies);
        recorder.clear();
        var agent = dependencies.agent();
        log.info("start training");
        for (int i = 0; i < dependencies.getnEpisodes(); i++) {
            var experiences = creator.getExperiences();
            var msr = msrGenerator.generate(experiences);
         //   var pm=fitAgentFromNewExperiences(msr);
         //   recorder.add(pm);
         //   log(experiences, i);
        }

    }



}
