package safe_rl.domain.trainer.helpers;

import com.google.common.base.Preconditions;
import common.list_arrays.ListUtils;
import common.other.Conditionals;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.trainer.value_objects.*;

import java.util.List;
import java.util.stream.IntStream;

/***
 This class evaluates an episode to derive data/results used to train an agent.
 Details explained in multi_step_return_calculation.md
 */

@AllArgsConstructor
@Log
public class MultiStepResultsGenerator<V> {

    /**
     * EvaluateResult is output from evaluateRewardsSum()
     */

    public record EvaluateResult<V>(
            Double sumRewardsNSteps,
            StateI<V> stateFuture,
            boolean isFutureStateOutside,
            boolean isFutureTerminal) {

        boolean isFutureOutsideOrTerminal() {
            return isFutureTerminal || isFutureStateOutside;
        }
    }

    @NonNull TrainerParameters parameters;
    @NonNull AgentACDiscoI<V> agent;

    @SneakyThrows
    public MultiStepResults<V> generate(List<Experience<V>> experiences) {
        int nExperiences = experiences.size();  //in literature often named T
        var informer = new EpisodeInfo<>(experiences);
        var results = MultiStepResults.<V>create(nExperiences);
        IntStream.range(0, nExperiences).forEach(   //end exclusive
                t -> {
                    var msr = createMultiStepResultAtTime(t, informer);
                    results.add(msr);
                }
        );
        return results;
    }

    private MultiStepResultItem<V> createMultiStepResultAtTime(int t, EpisodeInfo<V> informer) {
        var rs = evaluateRewardsSum(t, informer);
        double sumRewards = rs.sumRewardsNSteps();
        double valueTarget = rs.isFutureOutsideOrTerminal()
                ? sumRewards
                : sumRewards + parameters.gammaPowN() * agent.readCritic(rs.stateFuture());
        var singleStepExperience = informer.experienceAtTime(t);
        double vState = agent.readCritic(singleStepExperience.state());
        double advantage = valueTarget - vState;
        return MultiStepResultItem.<V>builder()
                .state(singleStepExperience.state())
                .actionApplied(singleStepExperience.actionApplied())
                .sumRewards(rs.sumRewardsNSteps())
                .stateFuture(rs.isFutureOutsideOrTerminal() ? null : rs.stateFuture())
                .isStateFutureTerminalOrNotPresent(rs.isFutureOutsideOrTerminal())
                .valueTarget(valueTarget)
                .advantage(advantage)
                .actionPolicy(singleStepExperience.ars().action())
                .isSafeCorrected(singleStepExperience.isSafeCorrected())
                .build();
    }

    /***
     * Used to derive return (sum rewards) at tStart for given experienceList
     *  n-step return si Gk:k+n=R(k)...gamma^(n-1)*R(k+n-1)+gamma^n*V(S(k+n-1))
     *  k is referring to experience index
     *  therefore
     *  Gk=(k=0,1=2, gamma=1)=R0+V(stateNew(0))   (standard TD)
     *  Gk=(k=0,n=2, gamma=1)=R0+R1+V(stateNew(1))
     *  A basic principle is that reward for stepping into terminal is included but value
     *  of terminal stateNew is zero
     */

    //public <=> to enable testing
    public EvaluateResult<V> evaluateRewardsSum(int tStart, List<Experience<V>> experiences) {
        return evaluateRewardsSum(tStart, new EpisodeInfo<>(experiences));
    }

    EvaluateResult<V> evaluateRewardsSum(int tStart, EpisodeInfo<V> informer) {
        int nExperiences = informer.size();
        Preconditions.checkArgument(tStart < nExperiences, "Non valid start index, tStart=" + tStart);
        int idxEnd = tStart + parameters.stepHorizon();
        var rewards = IntStream.rangeClosed(tStart, Math.min(idxEnd, nExperiences - 1))  //end inclusive
                .mapToObj(t -> informer.experienceAtTime(t).rewardApplied()).toList();
        double rewardSumDiscounted = ListUtils.discountedSum(rewards, parameters.gamma());
        boolean isEndOutSide = idxEnd > nExperiences - 1;
        maybeLog(nExperiences, idxEnd, isEndOutSide);
        StateI<V> stateFuture = isEndOutSide
                ? null
                : informer.experienceAtTime(idxEnd - 1).stateNextApplied();
        boolean isFutureTerminal = !isEndOutSide && informer.experienceAtTime(idxEnd).isTerminalApplied();
        return new EvaluateResult<>(rewardSumDiscounted, stateFuture, isEndOutSide, isFutureTerminal);
    }

    private static void maybeLog(int nExperiences, int idxEndExperience, boolean isEndOutSide) {
        Conditionals.executeIfTrue(isEndOutSide, () ->
                log.fine("Index end experience is outside, idxEndExperience=" +
                        idxEndExperience + ", nExperiences= " + nExperiences));
    }

}
