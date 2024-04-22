package safe_rl.helpers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.MultiStepResults;
import safe_rl.domain.value_classes.SingleResultMultiStepper;
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


    @SneakyThrows
    public MultiStepResults<V> generate(List<Experience<V>> experiences) {
        int nExperiences = experiences.size();  //can also be named T
        var informer=new EpisodeInfo<>(experiences);
        var evaluator = new MultiStepReturnEvaluator<>(parameters, experiences);
        var results = MultiStepResults.<V>create(nExperiences);
        IntStream.range(0, nExperiences).forEach(
                t -> addResultsAtTimeStep(results, evaluator, informer, t));
        throwIfBadResultFormat(results);
        return results;
    }

    void addResultsAtTimeStep(MultiStepResults<V> results,
                                      MultiStepReturnEvaluator<V> evaluator,
                                      EpisodeInfo<V> episodeInfo,
                                      int t) {
        Experience<V> experience=episodeInfo.experienceAtTime(t);
        var resMS = evaluator.getResultManySteps(t);
        addValueTarget(results, resMS);
        results.addState(experience.state());
        addAdvantage(results, experience);
        results.addActionApplied(experience.actionApplied());
        results.addActionPolicy(experience.ars().action());
        results.addIsSafeCorrect(experience.isSafeCorrected());
    }

    void addValueTarget(MultiStepResults<V> results, SingleResultMultiStepper<V> resMS) {
        double sumRewards = resMS.sumRewardsNSteps();
        Integer n = parameters.stepHorizon();
        double gammaPowN = Math.pow(parameters.gamma(), n);
        boolean isFutureOutsideOrTerminal =
                resMS.isFutureStateOutside() || resMS.isFutureTerminal();
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
        if (!results.isEqualListLength()) {
            throw new IllegalArgumentException("Non equal list lengths");
        }
    }

}
