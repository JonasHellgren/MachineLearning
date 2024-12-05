package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.helpers.EpisodeCreator;
import book_rl_explained.lunar_lander.helpers.ExperiencesInfo;
import book_rl_explained.lunar_lander.helpers.ProgressMeasures;
import book_rl_explained.lunar_lander.helpers.RecorderTrainingProgress;
import com.beust.jcommander.internal.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;
import org.hellgren.utilities.conditionals.Conditionals;

import java.util.List;

@AllArgsConstructor
@Getter
@Log
public class TrainerLunar implements TrainerI {
    public static final double VALUE_TERM = 0d;

    TrainerDependencies dependencies;
    RecorderTrainingProgress recorder;

    public static TrainerLunar of(TrainerDependencies dependencies) {
        return new TrainerLunar(dependencies, RecorderTrainingProgress.empty());
    }

    @Override
    public void train() {
        var creator = new EpisodeCreator(dependencies);
        recorder.clear();
        for (int i = 0; i < getnEpisodes(); i++) {
            var experiences = creator.getExperiences();
            var pm=fitAgentFromNewExperiences(experiences);
            recorder.add(pm);
            log(experiences, i);
        }
    }

    private static void log(List<ExperienceLunar> experiences, int i) {
        boolean isFail = ExperiencesInfo.of(experiences).endExperience().isFail();
        Conditionals.executeIfFalse(isFail, () -> log.fine("Yes, lunar landed safely!. Episode=: " + i));
    }


    public ProgressMeasures fitAgentFromNewExperiences(List<ExperienceLunar> experiences) {
        var agent = dependencies.agent();
        var tdList= Lists.<Double>newArrayList();
        var logStdList= Lists.<Double>newArrayList();
        for (ExperienceLunar experience : experiences) {
            double e = calculateTemporalDifferenceError(experience);
            var mAndStd=agent.fitActor(experience.state(),experience.action(), e);
            agent.fitCritic(experience.state(),e);
            tdList.add(e);
            logStdList.add(mAndStd.std());
        }

        return ProgressMeasures.of(experiences, tdList,logStdList);

    }

    private double calculateTemporalDifferenceError(ExperienceLunar experience) {
        var agent = dependencies.agent();
        double v = agent.readCritic(experience.state());
        double vNext = experience.isTerminal()
                ? VALUE_TERM
                : agent.readCritic(experience.stateNew());
        return experience.reward() + getGamma() * vNext - v;
    }

    private double getGamma() {
        return dependencies.trainerParameters().gamma();
    }

    private int getnEpisodes() {
        return dependencies.trainerParameters().nEpisodes();
    }

}
