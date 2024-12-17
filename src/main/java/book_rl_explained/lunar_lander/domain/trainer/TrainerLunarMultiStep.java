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

import java.util.List;

@AllArgsConstructor
@Getter
@Log
public class TrainerLunarMultiStep implements TrainerI {
    public static final double VALUE_TERM = 0d;

    TrainerDependencies dependencies;
    RecorderTrainingProgress recorder;

    public static TrainerLunarMultiStep of(TrainerDependencies dependencies) {
        return new TrainerLunarMultiStep(dependencies, RecorderTrainingProgress.empty());
    }

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
            //var pm=fitAgentFromNewExperiences(msr);

            var pm=updateActor(msr,experiences);
            updateCritic(msr);

            pm=addValuesSpecifStates(agent,pm);
            recorder.add(pm);
            log(experiences, i);
        }

    }

    private static ProgressMeasures addValuesSpecifStates(AgentI agent, ProgressMeasures pm) {
        double stateValuePos2Spd0= agent.readCritic(StateLunar.of(2,0));
        pm = pm.withStateValuePos2Spd0(stateValuePos2Spd0);
        double stateValuePos5Spd2= agent.readCritic(StateLunar.of(5,2));
        pm = pm.withStateValuePos5Spd2(stateValuePos5Spd2);
        return pm;
    }

    private static void log(List<ExperienceLunar> experiences, int i) {
        boolean isFail = ExperiencesInfo.of(experiences).endExperience().isTransitionToFail();
        Conditionals.executeIfFalse(isFail, () -> log.fine("Yes, lunar landed safely!. Episode=: " + i));
    }

    private ProgressMeasures updateActor(MultiStepResults msr, List<ExperienceLunar> experiences) {
        var agent=dependencies.agent();
        var tdList= Lists.<Double>newArrayList();
        var stdList= Lists.<Double>newArrayList();

        for (int step = 0; step < msr.nExperiences(); step++) {
            agent.fitActor(msr.stateAtStep(step),msr.actionAtStep(step),msr.advantageAtStep(step));
            tdList.add(Math.abs(msr.tdErrorAtStep(step)));
            var mAndStd=agent.readActor(msr.experienceAtStep(step).state());
            stdList.add(mAndStd.std());
        }
        return ProgressMeasures.of(experiences,tdList,stdList);
    }

    private void updateCritic(MultiStepResults msr) {
        var agent=dependencies.agent();
        for (int step = 0; step < msr.nExperiences() ; step++) {
            agent.fitCritic(msr.stateAtStep(step),  //msr.advantageAtStep(step));
                    msr.valueTarAtStep(step)- agent.readCritic(msr.stateAtStep(step)));
        }
    }




}
