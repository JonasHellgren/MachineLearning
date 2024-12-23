package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.helpers.*;
import book_rl_explained.radial_basis.TrainData;
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

            updateActorStep(msr);
            updateCriticStep(msr);


/*
            updateActorBatch(msr);
            updateCriticBatch(msr);

*/
            recorder.add(pmCreator.progressMeasures(experiences));
        }
    }

    private void updateActorBatch(MultiStepResults msr) {
        var dataMean = TrainData.emptyFromErrors();
        var dataStd = TrainData.emptyFromErrors();
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
        }
        agent.fitActorBatch(dataMean, dataStd);
    }

    private void updateActorStep(MultiStepResults msr) {
        var agent = dependencies.agent();
        var ap = agent.getAgentParameters();
        var tp = dependencies.trainerParameters();

        for (int step = 0; step < msr.nExperiences(); step++) {
            double adv = tp.clipAdvantage(msr.advantageAtStep(step));
            var state = msr.stateAtStep(step);
            var grad = agent.gradientMeanAndLogStd(state, msr.actionAtStep(step));
            grad = grad.clip(ap.gradMeanMax(), ap.gradStdMax());
            //todo clear istället
            var dataMean = TrainData.emptyFromErrors();
            var dataStd = TrainData.emptyFromErrors();
            dataMean.addInAndError(state.asList(), grad.mean() * adv);
            dataStd.addInAndError(state.asList(), grad.std() * adv);
            agent.fitActorBatch(dataMean, dataStd);
        }

/*        var agent = dependencies.agent();
        var tp = dependencies.trainerParameters();
        for (int step = 0; step < msr.nExperiences(); step++) {
            double adv = tp.clipAdvantage(msr.advantageAtStep(step));
            agent.fitActor(msr.stateAtStep(step), msr.actionAtStep(step), adv);
        }*/
    }

    private void updateCriticBatch(MultiStepResults msr) {
        var data = TrainData.emptyFromErrors();
        for (int step = 0; step < msr.nExperiences(); step++) {
            double adv = dependencies.trainerParameters().clipAdvantage(msr.advantageAtStep(step));
            data.addInAndError(msr.stateAtStep(step).asList(), adv);
        }
        dependencies.agent().fitCriticBatch(data);


        /*var tp = dependencies.trainerParameters();
        List<StateLunar> states = new ArrayList<>();
        List<Double> errors = new ArrayList<>();
        for (int step = 0; step < msr.nExperiences(); step++) {
            double adv = tp.clipAdvantage(msr.advantageAtStep(step));
            states.add(msr.stateAtStep(step));
            errors.add(adv);
        }
        dependencies.agent().fitCriticBatch(states,errors);*/
    }

    private void updateCriticStep(MultiStepResults msr) {
        var data = TrainData.emptyFromErrors();
        var tp = dependencies.trainerParameters();

        for (int step = 0; step < msr.nExperiences(); step++) {
            //double adv = dependencies.trainerParameters().clipAdvantage(msr.advantageAtStep(step));
            double adv = msr.advantageAtStep(step);
            adv = tp.clipTdError(adv);
            data.clear();
            data.addInAndError(msr.stateAtStep(step).asList(), adv);
            dependencies.agent().fitCriticBatch(data);


        }
/*

        var agent = dependencies.agent();
//        var tp = dependencies.trainerParameters();
        for (int step = 0; step < msr.nExperiences(); step++) {
            double e0 = msr.advantageAtStep(step);
            agent.fitCritic(msr.stateAtStep(step), tp.clipTdError(e0));
        }

*/
    }

}
