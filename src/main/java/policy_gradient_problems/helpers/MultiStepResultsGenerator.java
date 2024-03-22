package policy_gradient_problems.helpers;

import common.Conditionals;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.agent_interfaces.NeuralCriticI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.MultiStepResults;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.List;
import java.util.stream.IntStream;

/***
 This class evaluates an episode to derive data/results used to train an agent. Time steps between 0 and tEnd are used.
 So time t is in [0,tEnd].  An episode ends at time tEndep, see figure below.
 A critical part is the target value, it is defined in addValueTarget(sumRewards + valueFut).
 The term valueFut makes things a bit tricky, hence a deeper explanation follows.
 If isFutureStateOutside() is true, t+tHor is outside the end of the episode, hence valueFut must be zero.

 |_____|_____|_____|_____|_____|_____|_____|_____|_____|
 t               tEndep      t+tHor

 If isEndExperienceFail() is true the episode ended in fail state. In this case we want tEnd to cover the entire episode.
 The reason is that we want to learn also from steps/states close to tEndep. This is done by setting tEnd as
 nofExperiences.
 If isEndExperienceFail() is false the episode ended in non fail state.  In this case tEnd must be restricted
 so wrong learning not will take place from steps outside tEndEp. This is done by setting tEnd as
 nofExperiences-n+1.
 Or phrased differently, only states present earlier in the episode will be updated if not ending in fail state.
 The reason is that future, t+tHorizon, state values are not known for states later in the episode.

 */

@AllArgsConstructor
@Log
public class MultiStepResultsGenerator<V> {
    @NonNull TrainerParameters parameters;
    @NonNull NeuralCriticI<V> agent;

    public MultiStepResults generate(List<Experience<V>> experiences) {
        Integer n = parameters.stepHorizon();
        int nofExperiences = experiences.size();
        double gammaPowN = Math.pow(parameters.gamma(), n);
        var mse = new MultiStepReturnEvaluator<>(parameters, experiences);
        int tEnd = mse.isEndExperienceFail() ? nofExperiences : nofExperiences - n + 1;  //explained in top of file

        Conditionals.executeIfTrue(!mse.isEndExperienceFail(), () -> log.fine("Non ending in fail"));

        var results = MultiStepResults.create(tEnd, nofExperiences);
        IntStream.range(0, tEnd).forEach(t -> {
            var resMS = mse.getResultManySteps(t);
            double sumRewards = resMS.sumRewardsNSteps();
            double valueFut = !resMS.isFutureStateOutside()
                    ? gammaPowN * agent.getCriticOut(resMS.stateFuture())
                    : 0;
            results.addStateValues(mse.getExperience(t).state().asList());
            results.addAction(mse.getExperience(t).action());
            double criticOut = agent.getCriticOut(mse.getExperience(t).state());
            results.addPresentValue(criticOut);
            results.addValueTarget(sumRewards + valueFut);
        });
        return results;
    }

}
