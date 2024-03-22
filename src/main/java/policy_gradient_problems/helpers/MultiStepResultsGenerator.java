package policy_gradient_problems.helpers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import policy_gradient_problems.domain.agent_interfaces.NeuralCriticI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.MultiStepResults;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.List;
import java.util.stream.IntStream;

@AllArgsConstructor
public class MultiStepResultsGenerator<V> {


    @NonNull TrainerParameters parameters;
    @NonNull NeuralCriticI<V> agent;

    public MultiStepResults generate(List<Experience<V>> experiences) {
        Integer n = parameters.stepHorizon();
        int nofExperiences = experiences.size();
        double gammaPowN = Math.pow(parameters.gamma(), n);
        var elInfo = new MultiStepReturnEvaluator<>(experiences, parameters);
        int tEnd = elInfo.isEndExperienceFail() ? nofExperiences : nofExperiences - n + 1;  //explained in top of file

        var results = MultiStepResults.create(tEnd);
        IntStream.range(0, tEnd).forEach(t -> {
            var resMS = elInfo.getResultManySteps(t);
            double sumRewards = resMS.sumRewardsNSteps();
            double valueFut = !resMS.isFutureStateOutside()
                    ? gammaPowN * agent.getCriticOut(resMS.stateFuture())
                    : 0;
            results.addStateValues(elInfo.getExperience(t).state().asList());
            results.addAction(elInfo.getExperience(t).action());
            results.addValue(agent.getCriticOut(elInfo.getExperience(t).state()));
            results.addValueTarget(sumRewards + valueFut);
        });
        return results;
    }

}
