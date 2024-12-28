package lunar;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.trainer.TrainerLunarMultiStep;
import lombok.extern.java.Log;
import org.hellgren.utilities.list_arrays.ListCreator;
import java.util.List;
import static lunar.RunnerHelper.getDependencies;
import static lunar.RunnerHelper.plotNStepResults;
import static org.hellgren.utilities.list_arrays.MyListUtils.findAverage;

@Log
public class RunnerStepHorizonEvaluator {
    public static final int N_EPISODES = 3_000;
    public static final int N_EVALS_PER_N = 5;
    static final List<Integer> N_STEPS_LIST = ListCreator.createFromStartWithStepWithNofItems(1,1,12);

    public static void main(String[] args) {
        var ep = LunarProperties.defaultProps();
        var results = evaluateNSteps(ep, N_STEPS_LIST);
        plotNStepResults(results, N_STEPS_LIST);
    }

    static RunnerHelper.ResultsNStep evaluateNSteps(LunarProperties ep, List<Integer> nStepsList) {
        var results = RunnerHelper.ResultsNStep.empty();
        for (var nSteps : nStepsList) {
            log.info("nSteps: " + nSteps);
            for (int i = 0; i < N_EVALS_PER_N; i++) {
                var trainer = TrainerLunarMultiStep.of(getDependencies(ep, nSteps, N_EPISODES));
                trainer.train();
                var sumRewardsList = trainer.getRecorder().trajectory("Sum rewards");
                double sumRewardAverage = findAverage(sumRewardsList).orElseThrow();
                results.add(nSteps, sumRewardAverage);
            }
        }
        return results;
    }
}
