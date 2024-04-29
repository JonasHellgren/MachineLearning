package safe_rl.helpers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.ExperienceMultiStep;
import safe_rl.domain.value_classes.MultiStepResults;
import safe_rl.domain.value_classes.TrainerParameters;

import java.util.List;
import java.util.stream.IntStream;

import static common.other.Conditionals.executeOneOfTwo;

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
        IntStream.range(0, nExperiences).forEach(  //end exclusive
                t -> addResultsAtStep(results,informer, t));
        //throwIfBadResultFormat(results);
        return results;
    }

    void addResultsAtStep(MultiStepResults<V> results,
                          EpisodeInfo<V> informer,
                          int t) {
        var experience=informer.experienceAtTime(t);

        var resMS = evaluator.evaluate(t);
        double sumRewards = resMS.sumRewardsNSteps();
        boolean isFutureOutsideOrTerminal=resMS.isFutureTerminal() || resMS.isFutureStateOutside();
        double valueTarget=isFutureOutsideOrTerminal
                ? sumRewards
                : sumRewards + parameters.gammaPowN() * agent.readCritic(resMS.stateFuture());
        double vState = agent.readCritic(experience.state());
        var vStateFut = agent.readCritic(experience.stateNextApplied());
        double adv=experience.rewardApplied() + parameters.gamma() * vStateFut - vState;

        var expMs= ExperienceMultiStep.<V>builder()
                .state(experience.state())
                .actionApplied(experience.actionApplied())
                .sumRewards(resMS.sumRewardsNSteps())
                .stateFuture(isFutureOutsideOrTerminal?null: resMS.stateFuture())
                .isStateFutureTerminalOrNotPresent(isFutureOutsideOrTerminal)
                .valueTarget(valueTarget)
                .advantage(adv)
                .actionPolicy(experience.ars().action())
                .isSafeCorrected(experience.isSafeCorrected())
                .build();
        results.add(expMs);

    }

}
