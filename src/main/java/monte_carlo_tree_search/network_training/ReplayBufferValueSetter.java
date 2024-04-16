package monte_carlo_tree_search.network_training;

import common.list_arrays.ListUtils;
import common.math.MathUtils;
import lombok.Getter;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * This class has the objective to define the value field in an experience of bufferEpisode
 * Two variants are present: 1) createBufferFromReturns  2) createBufferFromStartReturn
 * 1) is like the "traditional" monte carlo updating, 2) pastes only the very initial return on all experiences
 *
 * The parameter fractionToInclude controls the portion of the episode experiences to regard. If small,
 * only the very first experience(s) will be regarded. The motivation is that for some domain, cart pole, for ex
 * the return in the end of the episode is faulty/miss leading. In the cart pole domain, for ex, a episode
 * can terminated due to failure or after a specific number of steps. In the later case, a good state
 * can erroneously labeled as bad (low return).
 *
 */

@Log
@Getter
public class ReplayBufferValueSetter<S,A> {

    ReplayBuffer<S,A> bufferEpisode;
    double discountFactor;
    boolean isFirstVisit;
    List<Double> returns;

    public ReplayBufferValueSetter(ReplayBuffer<S,A> bufferEpisode, double discountFactor, boolean isFirstVisit) {
        this.bufferEpisode = bufferEpisode;
        this.discountFactor = discountFactor;
        this.isFirstVisit = isFirstVisit;
        this.returns=new ArrayList<>();
    }

    public ReplayBuffer<S,A> createBufferFromReturns(double fractionToInclude) {
        BiFunction<List<Double>,Integer,Double> function=(list,idx) -> list.get(idx);
        return createBuffer(function,getIndexLimit(fractionToInclude));
    }

    public ReplayBuffer<S,A> createBufferFromStartReturn(double fractionToInclude) {
        BiFunction<List<Double>,Integer,Double> function=(list,idx) -> list.get(0);
        return createBuffer(function,getIndexLimit(fractionToInclude));
    }

    private ReplayBuffer<S,A> createBuffer(BiFunction<List<Double>,Integer,Double> function,int indexLimit) {
        ReplayBuffer<S,A> bufferWithValues=new ReplayBuffer<>(bufferEpisode.size());
        if (bufferEpisode.size()==0)  {
            log.warning("Empty episode buffer");
            return bufferWithValues;
        }
        setValuesInExperiences(function, bufferWithValues);
        return getLimitedPartOfBuffer(indexLimit, bufferWithValues);
    }

    @NotNull
    private ReplayBuffer<S, A> getLimitedPartOfBuffer(int iLimit, ReplayBuffer<S, A> bufferEpisodeUpdated) {
        ReplayBuffer<S,A> bufferEpisodeCut=new ReplayBuffer<>(iLimit);
        for (int i = 0; i < Math.min(iLimit, bufferEpisodeUpdated.size()) ; i++) {
          bufferEpisodeCut.addExperience(bufferEpisodeUpdated.getExperience(i));
     }
        return bufferEpisodeCut;
    }

    private void setValuesInExperiences(BiFunction<List<Double>, Integer, Double> function,
                                        ReplayBuffer<S, A> bufferEpisodeUpdated) {
        returns = createReturns();
        for (int i = bufferEpisode.size()-1; i >=0 ; i--) {
            Experience<S,A> experience= bufferEpisode.getExperience(i);
            if (isFirstVisitFlagTrueAndIsFirstVisit(i, experience)) {
                double value = function.apply(returns, i);
                addExperience(bufferEpisodeUpdated, experience, value);
            }
        }
        Collections.reverse(bufferEpisodeUpdated.getBuffer());
    }

    private boolean isFirstVisitFlagTrueAndIsFirstVisit(int i, Experience<S,A> experience) {
        return isFirstVisit && !bufferEpisode.isExperienceWithStateVariablesPresentBeforeIndex(experience.stateVariables, i);
    }

    private int getIndexLimit(double fractionToInclude) {
        return (int) MathUtils.clip(Math.round(fractionToInclude*bufferEpisode.size()),1,bufferEpisode.size());
    }

    private void addExperience(ReplayBuffer<S,A> bufferEpisodeUpdated, Experience<S,A> experience, double value) {
        bufferEpisodeUpdated.addExperience(Experience.<S,A>builder()
                .stateVariables(experience.stateVariables)
                .action(experience.action)
                .value(value)
                .reward(experience.reward)
                .build());
    }

    /**
     * rewards = 1d,10d,10d , df=0.5-> rewardsDiscounted =  0.25d,5d,10d -> returns = 15.25, 15, 10
     */

    private List<Double> createReturns() {
        List<Double> rewards=bufferEpisode.getBuffer().stream().map(e -> e.reward).collect(Collectors.toList());
        List<Double> rewardsDiscounted  = ListUtils.discountedElementsReverse(rewards,discountFactor);
        return ListUtils.getReturns(rewardsDiscounted);
    }


}
