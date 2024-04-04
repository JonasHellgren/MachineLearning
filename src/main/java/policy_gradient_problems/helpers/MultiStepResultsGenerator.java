package policy_gradient_problems.helpers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.agent_interfaces.NeuralCriticI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.MultiStepResults;
import policy_gradient_problems.domain.value_classes.ResultManySteps;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.List;
import java.util.stream.IntStream;

import static common.Conditionals.executeIfTrue;
import static common.Conditionals.executeOneOfTwo;

/***
 This class evaluates an episode to derive data/results used to train an agent.
 Details explained in multi_step_return_calculation.md
 */

@AllArgsConstructor
@Log
public class MultiStepResultsGenerator<V> {
    @NonNull TrainerParameters parameters;
    @NonNull NeuralCriticI<V> agent;

    public MultiStepResults generate(List<Experience<V>> experiences) {
        Integer n = parameters.stepHorizon();
        int nofExperiences = experiences.size();  //can also be named T
        var mse = new MultiStepReturnEvaluator<>(parameters, experiences);
        int tEnd = mse.isEndExperienceFail() ? nofExperiences : nofExperiences - n + 1;
        executeIfTrue(!mse.isEndExperienceFail(), () -> log.fine("Non ending in fail"));
        var results = MultiStepResults.create(tEnd, nofExperiences);
        double gammaPowN = Math.pow(parameters.gamma(), n);
        IntStream.range(0, tEnd).forEach(t -> {
            var resMS = mse.getResultManySteps(t);
            addValueTarget(gammaPowN, results, resMS);
            addOther(mse, results, t);
        });
        return results;
    }

    private void addValueTarget(double gammaPowN, MultiStepResults results, ResultManySteps<V> resMS) {
        double sumRewards = resMS.sumRewardsNSteps();
        executeOneOfTwo(resMS.isFutureStateOutside(),
                () -> results.addValueTarget(sumRewards),
                () -> results.addValueTarget(sumRewards + gammaPowN * agent.criticOut(resMS.stateFuture())));
    }

    private void addOther(MultiStepReturnEvaluator<V> mse, MultiStepResults results, int t) {
        results.addStateValues(mse.getExperience(t).state().asList());
        results.addAction(mse.getExperience(t).action());
        results.addProbAction(mse.getExperience(t).probAction());
        double criticOut = agent.criticOut(mse.getExperience(t).state());
        results.addCriticValue(criticOut);
    }

}
