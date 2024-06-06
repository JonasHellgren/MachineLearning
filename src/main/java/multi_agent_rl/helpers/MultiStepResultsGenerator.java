package multi_agent_rl.helpers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import multi_agent_rl.domain.abstract_classes.AgentI;
import multi_agent_rl.domain.value_classes.*;
import multi_agent_rl.helpers.*;

import java.util.List;
import java.util.stream.IntStream;

/***
 This class evaluates an episode to derive data/results used to train an agent.
 Details explained in multi_step_return_calculation.md
 */

@AllArgsConstructor
@Log
public class MultiStepResultsGenerator<V,O> {
    @NonNull TrainerParameters parameters;
    @NonNull AgentI<O> agent;

    final MultiStepReturnEvaluator<V,O> evaluator=new MultiStepReturnEvaluator<>();

    @SneakyThrows
    public MultiStepResults<V,O> generate(List<Experience<V,O>> experiences) {
        int nExperiences = experiences.size();  //in literature often named T
        var informer=new EpisodeInfo<>(experiences);
        evaluator.setParametersAndExperiences(parameters, experiences);
        var results = MultiStepResults.<V,O>create(nExperiences);
        IntStream.range(0, nExperiences).forEach(   //end exclusive
                t -> addResultsAtStep(results,informer, t));
        return results;
    }

    void addResultsAtStep(MultiStepResults<V,O> results,
                          EpisodeInfo<V,O> informer,
                          int t) {
        var singleStepExperience=informer.experienceAtTime(t);
        var resMS = evaluator.evaluate(t);
        double sumRewards = resMS.sumRewardsNSteps();
        boolean isFutureOutsideOrTerminal=resMS.isFutureTerminal() ||
                resMS.isFutureStateOutside();
        double valueTarget=isFutureOutsideOrTerminal
                ? sumRewards
                : sumRewards + parameters.gammaPowN() * agent.criticOut(resMS.stateFuture().getObservation(agent.getId()));
        double vState = agent.criticOut(singleStepExperience.state().getObservation(agent.getId()));
        double advantage=valueTarget-vState;

        var expMs= MultiStepResultItem.<V,O>builder()
                .state(singleStepExperience.state())
                .action(singleStepExperience.action())
                .sumRewards(resMS.sumRewardsNSteps())
                .stateFuture(isFutureOutsideOrTerminal?null: resMS.stateFuture())
                .isStateFutureTerminalOrNotPresent(isFutureOutsideOrTerminal)
                .valueTarget(valueTarget)
                .advantage(advantage)
                .build();
        results.add(expMs);

    }

}
