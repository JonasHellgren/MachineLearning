package safe_rl.helpers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.value_classes.Experience;
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
        throwIfBadResultFormat(results);
        return results;
    }

    void addResultsAtStep(MultiStepResults<V> results,
                          EpisodeInfo<V> informer,
                          int t) {
        var experience=informer.experienceAtTime(t);
        results.addState(experience.state());
        addIsOutAndValueTarget(results,t);
        addAdvantage(results, experience);
        results.addActionApplied(experience.actionApplied());
        results.addActionPolicy(experience.ars().action());
        results.addIsSafeCorrect(experience.isSafeCorrected());
    }

    void addIsOutAndValueTarget(MultiStepResults<V> results, int t) {
        var resMS = evaluator.evaluate(t);
        double sumRewards = resMS.sumRewardsNSteps();
        Integer n = parameters.stepHorizon();
        double gammaPowN = Math.pow(parameters.gamma(), n);
        boolean isFutureOutsideOrTerminal =
                resMS.isFutureStateOutside() || resMS.isFutureTerminal();
        results.addIsFutureOutsideOrTerminal(isFutureOutsideOrTerminal);
        executeOneOfTwo(isFutureOutsideOrTerminal,
                () -> results.addValueTarget(sumRewards),
                () -> results.addValueTarget(sumRewards +
                        gammaPowN * agent.readCritic(resMS.stateFuture())));
    }

    void addAdvantage(MultiStepResults<V> results, Experience<V> experience) {
        double vState = agent.readCritic(experience.state());
        double reward = experience.rewardApplied();
        double vStateNext = agent.readCritic(experience.stateNextApplied());
        results.addAdvantage(reward + parameters.gamma() * vStateNext - vState);
    }

    private void throwIfBadResultFormat(MultiStepResults<V> results) {
        if (!results.isEqualListLength() || results.nExperiences()!=results.stateList().size()) {
            throw new IllegalArgumentException("Non correct list(s) length");
        }
    }

}
