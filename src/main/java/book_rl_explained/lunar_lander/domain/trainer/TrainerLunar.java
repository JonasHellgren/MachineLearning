package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.agent.AgentI;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.helpers.EpisodeCreator;
import book_rl_explained.lunar_lander.helpers.ExperiencesInfo;
import book_rl_explained.lunar_lander.helpers.ProgressMeasures;
import book_rl_explained.lunar_lander.helpers.RecorderTrainingProgress;
import com.beust.jcommander.internal.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;
import org.hellgren.utilities.conditionals.Conditionals;
import org.hellgren.utilities.math.MyMathUtils;

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
        var agent = dependencies.agent();
        log.info("start training");

        for (int i = 0; i < getnEpisodes(); i++) {
            var experiences = creator.getExperiences();
          //  System.out.println("experiences created");
          //  experiences.forEach(System.out::println);
            var pm=fitAgentFromNewExperiences(experiences);
            pm = addtMeasures(agent, pm);
            recorder.add(pm);
            log(experiences, i);
        }
    }

    private static ProgressMeasures addtMeasures(AgentI agent, ProgressMeasures pm) {
        double stateValuePos2Spd0= agent.readCritic(StateLunar.of(2,0));
        pm = pm.withStateValuePos2Spd0(stateValuePos2Spd0);
        double stateValuePos5Spd2= agent.readCritic(StateLunar.of(5,2));
        pm = pm.withStateValuePos5Spd2(stateValuePos5Spd2);
        return pm;
    }

    private static void log(List<ExperienceLunar> experiences, int i) {
        boolean isFail = ExperiencesInfo.of(experiences).endExperience().isFail();
        Conditionals.executeIfFalse(isFail, () -> log.fine("Yes, lunar landed safely!. Episode=: " + i));
    }


    public ProgressMeasures fitAgentFromNewExperiences(List<ExperienceLunar> experiences) {
        var agent = dependencies.agent();
        var tp = dependencies.trainerParameters();
        var tdList= Lists.<Double>newArrayList();
        var stdList= Lists.<Double>newArrayList();
        for (ExperienceLunar experience : experiences) {
            double e0 = calculateTemporalDifferenceError(experience);
            double e= MyMathUtils.clip(e0, -tp.tdMax(), tp.tdMax());
            agent.fitActor(experience.state(),experience.action(), e);
            agent.fitCritic(experience.state(),e);
            tdList.add(Math.abs(e));
            var mAndStd=agent.readActor(experience.state());
            stdList.add(mAndStd.std());
        }

        return ProgressMeasures.of(experiences, tdList,stdList);

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
