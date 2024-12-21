package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.helpers.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.ArrayList;
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
        var pmCreator = ProgressMeasuresCreator.of(dependencies);
        var msrGenerator = MultiStepResultsGenerator.of(dependencies);
        recorder.clear();
        log.info("starting training");
        for (int i = 0; i < dependencies.getnEpisodes(); i++) {
            var experiences = epCreator.experiences();
            var msr = msrGenerator.generate(experiences);
            updateActorStep(msr);
            updateCriticBatch(msr);
            recorder.add(pmCreator.progressMeasures(experiences));
        }
    }

    private void updateActorBatch(MultiStepResults msr) {
        var tp = dependencies.trainerParameters();
        List<StateLunar> states = new ArrayList<>();
        List<Double> actions = new ArrayList<>();
        List<Double> errors = new ArrayList<>();
        for (int step = 0; step < msr.nExperiences(); step++) {
            double adv = tp.clipAdvantage(msr.advantageAtStep(step));
            states.add(msr.stateAtStep(step));
            actions.add(msr.actionAtStep(step));
            errors.add(adv);
        }
        dependencies.agent().fitActorBatch(states,actions,errors);
    }

    private void updateActorStep(MultiStepResults msr) {
        var agent = dependencies.agent();
        var tp = dependencies.trainerParameters();
        for (int step = 0; step < msr.nExperiences(); step++) {
            double adv = tp.clipAdvantage(msr.advantageAtStep(step));
            agent.fitActor(msr.stateAtStep(step), msr.actionAtStep(step), adv);
        }
    }

    private void updateCriticBatch(MultiStepResults msr) {
        var tp = dependencies.trainerParameters();
        List<StateLunar> states = new ArrayList<>();
        List<Double> errors = new ArrayList<>();
        for (int step = 0; step < msr.nExperiences(); step++) {
            double adv = tp.clipAdvantage(msr.advantageAtStep(step));
            states.add(msr.stateAtStep(step));
            errors.add(adv);
        }
        dependencies.agent().fitCriticBatch(states,errors);
    }

    private void updateCriticStep(MultiStepResults msr) {
        var agent = dependencies.agent();
        var tp = dependencies.trainerParameters();
        for (int step = 0; step < msr.nExperiences(); step++) {
            double e0 = msr.advantageAtStep(step);
            agent.fitCritic(msr.stateAtStep(step), tp.clipTdError(e0));
        }
    }

}
