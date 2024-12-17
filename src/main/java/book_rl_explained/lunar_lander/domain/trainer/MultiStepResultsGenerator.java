package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.hellgren.utilities.reinforcement_learning.MyRewardListUtils;
import book_rl_explained.lunar_lander.helpers.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

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
        for (int time = 0; time < nExperiences; time++) {
            results.add(createResultAtTime(time, informer));
        }
        return results;
    }

    private MultiStepResult createResultAtTime(int time, EpisodeInfo informer) {
        var experience = informer.experienceAtTime(time);
        int nExperiences = informer.size();
        var parameters = dependencies.trainerParameters();
        int idxEnd = time + parameters.stepHorizon();
        Preconditions.checkArgument(time < nExperiences, "Non valid start index, time=" + time);
        Preconditions.checkArgument(idxEnd > time, "Non valid end index, idxEnd=" + idxEnd + ", time=" + time);
        var rewards = getRewards(time, informer, idxEnd, nExperiences);
        double rewardSum = MyRewardListUtils.discountedSum(rewards, parameters.gamma());
        boolean isEndStateOutSide = idxEnd > nExperiences - 1;
        StateLunar stateFuture = getStateFuture(informer, isEndStateOutSide, idxEnd);
        double valueTarget = calculator.valueOfTakingAction(isEndStateOutSide, stateFuture, rewardSum);
        double advantage = calculator.advantage(dependencies.agent(), experience.state(), valueTarget);
        double tdError = calculator.temporalDifferenceError(experience);
        return MultiStepResult.builder()
                .state(experience.state())
                .action(experience.action())
                .sumRewards(rewardSum)
                .stateFuture(stateFuture)
                .isStateFutureTerminalOrNotPresent(isEndStateOutSide)
                .valueTarget(valueTarget)
                .advantage(advantage)
                .tdError(tdError)
                .build();
    }

    /***
     * Rewards are based on [time, idxEnd - 1], where idxEnd=time+stepHorizon
     * Example stepHorizon = 1 => [time, time] => one reward
     * Example stepHorizon = 2 => [time, time+1] => two rewards (if time+1 is in episode)
     */

    private static List<Double> getRewards(int time, EpisodeInfo informer, int idxEnd, int nExperiences) {
        return IntStream.rangeClosed(time, Math.min(idxEnd - 1, nExperiences - 1))  //end inclusive
                .mapToObj(t -> informer.rewardAtTime(t)).toList();
    }

    /***
     *  idxEnd=time+stepHorizon  => stateFuture(time+stepHorizon)=stateFuture(idxEnd)=stateNewAtTime(idxEnd - 1)
     *  stateNewAtTime is the  new state when transitioning from state at time time, therefore -1
     */

    @Nullable
    private static StateLunar getStateFuture(EpisodeInfo informer, boolean isEndStateOutSide, int idxEnd) {
        return isEndStateOutSide ? null : informer.stateNewAtTime(idxEnd - 1);
    }



}
