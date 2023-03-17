package monte_carlo_tree_search.network_training;

import common.MathUtils;
import lombok.extern.java.Log;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * This class has the objective to define the value field in an experience of bufferEpisode
 * Two variants are present: 1) createBufferFromReturns  2) createBufferFromStartReturn
 * 1) is like the "traditional" monte carlo updating, 2) pastes the very initial return on all experiences
 *
 * The parameter fractionToInclude controls the portion of the episode experiences to regard. If small,
 * only the very first experience(s) will be regarded. The motivation is that for some domain, cart pole, for ex
 * the return in the end of the episode is faulty/miss leading. In the cart pole domain, for ex, a episode
 * can terminated due to failure or after a specific number of steps. In the later case, a good state
 * can erroneously labeled as bad (low return).
 *
 */

//todo make generic
@Log
public class ReplayBufferValueSetter<S,A> {

    ReplayBuffer<S,A> bufferEpisode;
    double discountFactor;
    double episodeReturn;
    boolean isFirstVisit;

    public ReplayBufferValueSetter(ReplayBuffer<S,A> bufferEpisode, double discountFactor, boolean isFirstVisit) {
        this.bufferEpisode = bufferEpisode;
        this.discountFactor = discountFactor;
        this.isFirstVisit = isFirstVisit;

    }

    public double getEpisodeReturn() {
        return episodeReturn;
    }

    public ReplayBuffer<S,A> createBufferFromReturns(double fractionToInclude) {
        int iLimit = getiLimit(fractionToInclude);

        BiFunction<List<Double>,Integer,Double> function=(list,idx) -> list.get(idx);
        return createBuffer(function,iLimit);
    }

    public ReplayBuffer<S,A> createBufferFromStartReturn(double fractionToInclude) {
        int iLimit = getiLimit(fractionToInclude);
        BiFunction<List<Double>,Integer,Double> function=(list,idx) -> list.get(0);
        return createBuffer(function,iLimit);
    }

    private int getiLimit(double fractionToInclude) {

        return (int) MathUtils.clip(Math.round(fractionToInclude*bufferEpisode.size()),1,bufferEpisode.size());

        //return (int) MathUtils.clip(Math.round(fractionToInclude*bufferEpisode.size()),1,bufferEpisode.size()-1);
    }

    public ReplayBuffer<S,A> createBuffer(BiFunction<List<Double>,Integer,Double> function,
                                                                 int iLimit) {

        ReplayBuffer<S,A> bufferEpisodeUpdated=new ReplayBuffer<>(bufferEpisode.size());

        if (bufferEpisode.size()==0)  {
            log.warning("Empty episode buffer");
            return bufferEpisodeUpdated;
        }

        List<Double> returns= createReturnList();
        for (int i = bufferEpisode.size()-1; i >=0 ; i--) {
            Experience<S,A> experience= bufferEpisode.getExperience(i);
            if (isFirstVisitFlagTrueAndIsFirstVisit(i, experience)) {
                double value = function.apply(returns, i);
                addExperience(bufferEpisodeUpdated, experience, value);
            }
        }
        Collections.reverse(bufferEpisodeUpdated.getBuffer());

        ReplayBuffer<S,A> bufferEpisodeCut=new ReplayBuffer<>(iLimit);
//        for (int i = 0; i < Math.min(iLimit,bufferEpisodeUpdated.size()-1) ; i++) {
          for (int i = 0; i < Math.min(iLimit,bufferEpisodeUpdated.size()) ; i++) {

            bufferEpisodeCut.addExperience(bufferEpisodeUpdated.getExperience(i));
       }
        return bufferEpisodeCut;
    }

    private boolean isFirstVisitFlagTrueAndIsFirstVisit(int i, Experience<S,A> experience) {
        return isFirstVisit && !bufferEpisode.isExperienceWithStateVariablesPresentBeforeIndex(experience.stateVariables, i);
    }

    private boolean isInSetOfIncludedExperiences(int i,int iLimit) {
        return i<iLimit;
    }

    private void addExperience(ReplayBuffer<S,A> bufferEpisodeUpdated, Experience<S,A> experience, double value) {
        bufferEpisodeUpdated.addExperience(Experience.<S,A>builder()
                .stateVariables(experience.stateVariables)
                .action(experience.action)
                .value(value)
                .reward(experience.reward)
                .build());
    }
    /***
     * rewards=[1 1 1], discountFactor=1 => returns = [1 2 3]  => returns = [3 2 1] (after reversed)
     */
    private List<Double> createReturnList() {
        List<Double> returns=new ArrayList<>();
        this.episodeReturn=0;
        for (int i = bufferEpisode.size()-1; i >=0 ; i--) {
            Experience<S,A> experience = bufferEpisode.getExperience(i);
            episodeReturn = discountFactor * episodeReturn +  experience.reward;
            returns.add(episodeReturn);
        }
        Collections.reverse(returns);
        return returns;
    }

}
