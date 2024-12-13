package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.hellgren.utilities.conditionals.Conditionals;
import org.hellgren.utilities.reinforcement_learning.MyRewardListUtils;
import book_rl_explained.lunar_lander.helpers.*;
import java.util.List;
import java.util.stream.IntStream;

@Log
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MultiStepResultsGenerator {

    TrainerDependencies dependencies;
    ValueAndAdvantageCalculator calculator;

    public static MultiStepResultsGenerator of(TrainerDependencies dependencies) {
        return new MultiStepResultsGenerator(dependencies, ValueAndAdvantageCalculator.of(dependencies));
    }

    @SneakyThrows
    public MultiStepResults generate(List<ExperienceLunar> experiences) {
        int nExperiences = experiences.size();  //in literature often named T
        var informer = EpisodeInfo.of(experiences);
        var results = MultiStepResults.create(nExperiences);
        IntStream.range(0, nExperiences).forEach(   //end exclusive
                t -> {
                    var msr = createMultiStepResultAtTime(t, informer);
                    results.add(msr);
                }
        );
        return results;
    }

    private MultiStepResultItem createMultiStepResultAtTime(int t, EpisodeInfo informer) {
        var se = informer.experienceAtTime(t);
        var rs = evaluateRewardsSum(t, informer);
        return createResult(se, rs);
    }
/*
    //public <=> to enable testing
    public EvaluateResult evaluateRewardsSum(int tStart, List<ExperienceLunar> experiences) {
        return evaluateRewardsSum(tStart, EpisodeInfo.of(experiences));
    }*/

    public MultiStepResultItem createResult(ExperienceLunar singleStepExperience,
                                            EvaluateResult rs) {
        var agent=dependencies.agent();
        double valueTarget = calculator.valueOfTakingAction(
                rs.isFutureOutsideOrTerminal(), rs.stateFuture(), rs.sumRewardsNSteps());
        StateLunar stateFuture = rs.isFutureOutsideOrTerminal() ? null : rs.stateFuture();
        return MultiStepResultItem.builder()
                .state(singleStepExperience.state())
                .action(singleStepExperience.action())
                .sumRewards(rs.sumRewardsNSteps())
                .stateFuture(stateFuture)
                .isStateFutureTerminalOrNotPresent(rs.isFutureOutsideOrTerminal())
                .valueTarget(valueTarget)
                .advantage(calculator.advantage(agent, singleStepExperience,rs))
                .build();
    }


    /***
     * Used to derive return (sum rewards) at tStart for given results
     *  n-step return si Gk:k+n=R(k)...gamma^(n-1)*R(k+n-1)+gamma^n*V(S(k+n-1))
     *  k is referring to experience index
     *  therefore
     *  Gk=(k=0,1=2, gamma=1)=R0+V(stateNew(0))   (standard TD)
     *  Gk=(k=0,n=2, gamma=1)=R0+R1+V(stateNew(1))
     *  A basic principle is that reward for stepping into terminal is included but value
     *  of terminal stateNew is zero
     */



    EvaluateResult evaluateRewardsSum(int tStart, EpisodeInfo informer) {
        int nExperiences = informer.size();
        Preconditions.checkArgument(tStart < nExperiences, "Non valid start index, tStart=" + tStart);
        var trainerParameters = dependencies.trainerParameters();
        int idxEnd = tStart + trainerParameters.stepHorizon();
        var rewards = IntStream.rangeClosed(tStart, Math.min(idxEnd-1, nExperiences - 1))  //end inclusive
                .mapToObj(t -> informer.experienceAtTime(t).reward()).toList();
        double rewardSumDiscounted = MyRewardListUtils.discountedSum(rewards, trainerParameters.gamma());
        boolean isEndOutSide = idxEnd > nExperiences - 1;
        maybeLog(nExperiences, idxEnd, isEndOutSide);
        StateLunar stateFuture = isEndOutSide
                ? null
                : informer.experienceAtTime(idxEnd - 1).stateNew();
        boolean isFutureTerminal = !isEndOutSide && informer.experienceAtTime(idxEnd).isTransitionToTerminal();
        return new EvaluateResult(rewardSumDiscounted, stateFuture, isEndOutSide, isFutureTerminal);
    }

    private static void maybeLog(int nExperiences, int idxEndExperience, boolean isEndOutSide) {
        Conditionals.executeIfTrue(isEndOutSide, () ->
                log.fine("Index end experience is outside, idxEndExperience=" +
                        idxEndExperience + ", nExperiences= " + nExperiences));
    }

}
