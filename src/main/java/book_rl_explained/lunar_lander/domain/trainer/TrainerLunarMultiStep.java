package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.helpers.*;
import book_rl_explained.radial_basis.TrainData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;

@AllArgsConstructor
@Getter
@Log
public class TrainerLunarMultiStep implements TrainerI {

    TrainerDependencies dependencies;
    RecorderTrainingProgress recorder;


    public static TrainerLunarMultiStep of(TrainerDependencies dependencies) {
        return new TrainerLunarMultiStep(dependencies,
                RecorderTrainingProgress.empty());
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

       //     updateStep(msr);
            fitBatch(msr);
            recorder.add(pmCreator.progressMeasures(experiences));
        }
    }

    private void fitBatch(MultiStepResults msr) {
        var dataMean = TrainData.emptyFromErrors();
        var dataStd = TrainData.emptyFromErrors();
        var data = TrainData.emptyFromErrors();
        var agent = dependencies.agent();
        var ap = agent.getAgentParameters();
        var tp = dependencies.trainerParameters();

        for (int step = 0; step < msr.nExperiences(); step++) {
            double adv = tp.clipAdvantage(msr.advantageAtStep(step));
            var state = msr.stateAtStep(step);
            var grad = agent.gradientMeanAndLogStd(state, msr.actionAtStep(step));
            grad = grad.clip(ap.gradMeanMax(), ap.gradStdMax());
            dataMean.addInAndError(state.asList(), grad.mean() * adv);
            dataStd.addInAndError(state.asList(), grad.std() * adv);
            data.addInAndError(msr.stateAtStep(step).asList(), adv);
        }
        agent.fitActorBatch(dataMean, dataStd);
        agent.fitCriticBatch(data);

    }

    private void fitStep(MultiStepResults msr) {
        var agent = dependencies.agent();
        var ap = agent.getAgentParameters();
        var tp = dependencies.trainerParameters();
        var dataMean = TrainData.emptyFromErrors();
        var dataStd = TrainData.emptyFromErrors();
        var data = TrainData.emptyFromErrors();

        for (int step = 0; step < msr.nExperiences(); step++) {
            double adv = tp.clipAdvantage(msr.advantageAtStep(step));
            var state = msr.stateAtStep(step);
            var grad = agent.gradientMeanAndLogStd(state, msr.actionAtStep(step));
            grad = grad.clip(ap.gradMeanMax(), ap.gradStdMax());
            dataMean.clear();
            dataStd.clear();
            data.clear();
            dataMean.addInAndError(state.asList(), grad.mean() * adv);
            dataStd.addInAndError(state.asList(), grad.std() * adv);
            data.addInAndError(msr.stateAtStep(step).asList(), adv);
            agent.fitActorBatch(dataMean, dataStd);
            agent.fitCriticBatch(data);
        }

    }

}
