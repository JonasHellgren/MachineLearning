package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.helpers.*;
import book_rl_explained.radial_basis.TrainData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;
import org.hellgren.utilities.conditionals.Conditionals;

import java.util.List;

@AllArgsConstructor
@Getter
@Log
public class TrainerLunarSingleStep implements TrainerI {

    TrainerDependencies dependencies;
    RecorderTrainingProgress recorder;
    ValueCalculator calculator;

    public static TrainerLunarSingleStep of(TrainerDependencies dependencies) {
        return new TrainerLunarSingleStep(
                dependencies,
                RecorderTrainingProgress.empty(),
                ValueCalculator.of(dependencies));
    }

    @Override
    public void train() {
        var epCreator = EpisodeCreator.of(dependencies);
        var pmCreator= ProgressMeasuresCreator.of(dependencies);
        recorder.clear();
        log.info("starting training");
        for (int i = 0; i < dependencies.getnEpisodes(); i++) {
            var experiences = epCreator.experiences();
            fitAgent(experiences);
            recorder.add(pmCreator.progressMeasures(experiences));
        }
    }


    public void fitAgent(List<ExperienceLunar> experiences) {
        var agent = dependencies.agent();
        var tp = dependencies.trainerParameters();
        var ap = agent.getAgentParameters();
        var dataMean = TrainData.emptyFromErrors();
        var dataStd = TrainData.emptyFromErrors();
        var data = TrainData.emptyFromErrors();
        for (ExperienceLunar experience : experiences) {
            double tdError = tp.clipTdError(calculator.temporalDifferenceError(experience));
            var state = experience.state();
            var grad = agent.gradientMeanAndLogStd(state, experience.action());
            grad = grad.clip(ap.gradMeanMax(), ap.gradStdMax());
            dataMean.addInAndError(state.asList(), grad.mean() * tdError);
            dataStd.addInAndError(state.asList(), grad.std() * tdError);
            data.addInAndError(state.asList(), tdError);

        }
        agent.fitActorBatch(dataMean, dataStd);
        agent.fitCriticBatch(data);
    }

/*    public void fitAgent0(List<ExperienceLunar> experiences) {
        var agent = dependencies.agent();
        var tp = dependencies.trainerParameters();
        for (ExperienceLunar experience : experiences) {
            double e = tp.clipTdError(calculator.temporalDifferenceError(experience));
            agent.fitActor(experience.state(), experience.action(), e);
            agent.fitCritic(experience.state(), e);
        }
    }*/

}
