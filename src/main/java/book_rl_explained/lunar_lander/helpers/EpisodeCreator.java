package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.environment.StepReturnLunar;
import book_rl_explained.lunar_lander.domain.trainer.ExperienceLunar;
import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import com.beust.jcommander.internal.Lists;
import common.other.Counter;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class EpisodeCreator {

    TrainerDependencies dependencies;

    public List<ExperienceLunar> getExperiences() {
        List<ExperienceLunar> experienceList = Lists.newArrayList();
        var agent=dependencies.agent();
        var stateStart=dependencies.startState();
        var counter= new Counter(dependencies.trainerParameters().nofStepsMax());
        var environment=dependencies.environment();
        StepReturnLunar sr=StepReturnLunar.ofNotFailAndNotTerminal();
        var state=stateStart.copy();
        while (sr.isNotTerminalAndNofStepsNotExceeded(counter)) {
            var action = agent.chooseAction(state);
            sr = environment.step(state, action);
            experienceList.add(ExperienceLunar.of(state, action, sr.reward(), sr.stateNew()));
            state=sr.stateNew();
            counter.increase();
        }
        return experienceList;
    }

}
