package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.trainer.ExperienceLunar;
import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import lombok.AllArgsConstructor;
import org.hellgren.utilities.conditionals.Conditionals;
import org.hellgren.utilities.conditionals.Counter;

@AllArgsConstructor
public class AgentEvaluator {

    TrainerDependencies dependencies;

    public double evaluate(int nEvals) {
        var creator = new EpisodeCreator(dependencies);
        var failCounter = new Counter();
        var evalCounter = new Counter(nEvals);
        while (!evalCounter.isExceeded()) {
            var experiencesNotExploring = creator.getExperiencesNotExploring();
            ExperienceLunar endExperience = ExperiencesInfo.of(experiencesNotExploring).endExperience();
            boolean isFail = endExperience.isFail();
            Conditionals.executeIfTrue(isFail, () -> failCounter.increase());
            evalCounter.increase();
        }
        return (double) failCounter.getCount() / nEvals;

    }


}
