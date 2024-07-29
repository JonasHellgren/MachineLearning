package safe_rl.domain.trainer.helpers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.MultiStepResultItem;
import safe_rl.domain.trainer.value_objects.MultiStepResults;
import safe_rl.domain.trainer.value_objects.TrainerParameters;

import java.util.List;
import java.util.stream.IntStream;

/***
 This class evaluates an episode to derive data/results used to train an agent.
 Details explained in multi_step_return_calculation.md
 */

@AllArgsConstructor
@Log
public class MultiStepResultsGenerator<V> {
    @NonNull TrainerParameters parameters;
    @NonNull AgentACDiscoI<V> agent;

    final MultiStepReturnEvaluator<V> evaluator=new MultiStepReturnEvaluator<>();

    @SneakyThrows
    public MultiStepResults<V> generate(List<Experience<V>> experiences) {
        int nExperiences = experiences.size();  //in literature often named T
        var informer=new EpisodeInfo<>(experiences);
        evaluator.setParametersAndExperiences(parameters, experiences);
        var results = MultiStepResults.<V>create(nExperiences);
        IntStream.range(0, nExperiences).forEach(   //end exclusive
                t -> addResultsAtStep(results,informer, t));
        return results;
    }

    void addResultsAtStep(MultiStepResults<V> results,
                          EpisodeInfo<V> informer,
                          int t) {
        var singleStepExperience=informer.experienceAtTime(t);
        var resMS = evaluator.evaluate(t);
        double sumRewards = resMS.sumRewardsNSteps();
        boolean isFutureOutsideOrTerminal=resMS.isFutureTerminal() ||
                resMS.isFutureStateOutside();
        double valueTarget=isFutureOutsideOrTerminal
                ? sumRewards
                : sumRewards + parameters.gammaPowN() * agent.readCritic(resMS.stateFuture());
        double vState = agent.readCritic(singleStepExperience.state());
        double advantage=valueTarget-vState;

        var expMs= MultiStepResultItem.<V>builder()
                .state(singleStepExperience.state())
                .actionApplied(singleStepExperience.actionApplied())
                .sumRewards(resMS.sumRewardsNSteps())
                .stateFuture(isFutureOutsideOrTerminal?null: resMS.stateFuture())
                .isStateFutureTerminalOrNotPresent(isFutureOutsideOrTerminal)
                .valueTarget(valueTarget)
                .advantage(advantage)
                .actionPolicy(singleStepExperience.ars().action())
                .isSafeCorrected(singleStepExperience.isSafeCorrected())
                .build();
        results.add(expMs);

    }

}
