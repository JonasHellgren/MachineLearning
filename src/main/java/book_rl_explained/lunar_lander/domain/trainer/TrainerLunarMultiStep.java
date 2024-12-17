package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.helpers.EpisodeCreator;
import book_rl_explained.lunar_lander.helpers.ExperiencesInfo;
import book_rl_explained.lunar_lander.helpers.ProgressMeasuresCreator;
import book_rl_explained.lunar_lander.helpers.RecorderTrainingProgress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;
import org.hellgren.utilities.conditionals.Conditionals;
import org.hellgren.utilities.math.MyMathUtils;
import java.util.List;

@AllArgsConstructor
@Getter
@Log
public class TrainerLunarMultiStep implements TrainerI {

    TrainerDependencies dependencies;
    RecorderTrainingProgress recorder;
    ValueCalculator calculator;


    public static TrainerLunarMultiStep of(TrainerDependencies dependencies) {
        return new TrainerLunarMultiStep(dependencies,
                RecorderTrainingProgress.empty(),
                ValueCalculator.of(dependencies));
    }

    @Override
    public void train() {
        var epCreator = EpisodeCreator.of(dependencies);
        var pmCreator= ProgressMeasuresCreator.of(dependencies);
        var msrGenerator = MultiStepResultsGenerator.of(dependencies);
        recorder.clear();
        log.info("starting training");
        for (int i = 0; i < dependencies.getnEpisodes(); i++) {
            var experiences = epCreator.experiences();
            var msr = msrGenerator.generate(experiences);
            updateActor(msr);
            updateCritic(msr);
            recorder.add(pmCreator.progressMeasures(experiences));
        }
    }

    private void updateActor(MultiStepResults msr) {
        var agent=dependencies.agent();
        var tp = dependencies.trainerParameters();
        for (int step = 0; step < msr.nExperiences(); step++) {
            double adv= tp.clipAdvantage(msr.advantageAtStep(step));
            agent.fitActor(msr.stateAtStep(step),msr.actionAtStep(step), adv);
        }
    }

    private void updateCritic(MultiStepResults msr) {
        var agent=dependencies.agent();
        var tp = dependencies.trainerParameters();
        for (int step = 0; step < msr.nExperiences() ; step++) {
            //double e0 = msr.valueTarAtStep(step) - agent.readCritic(msr.stateAtStep(step));
            //double e0 = msr.valueTarAtStep(step) - agent.readCritic(msr.stateAtStep(step));
            double e0= msr.advantageAtStep(step);
            agent.fitCritic(msr.stateAtStep(step), tp.clipTdError(e0));
        }
    }

}
