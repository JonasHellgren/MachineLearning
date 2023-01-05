package mcts_cart_pole_runner;

import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.ReplayBuffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

//todo make generic
public class ReplayBufferValueSetter {

    ReplayBuffer<CartPoleVariables, Integer> bufferEpisode;
    double discountFactor;
    double episodeReturn;
    boolean isFirstVisit;

    public ReplayBufferValueSetter(ReplayBuffer<CartPoleVariables, Integer> bufferEpisode, double discountFactor, boolean isFirstVisit) {
        this.bufferEpisode = bufferEpisode;
        this.discountFactor = discountFactor;
        this.isFirstVisit = isFirstVisit;

    }

    public double getEpisodeReturn() {
        return episodeReturn;
    }

    public ReplayBuffer<CartPoleVariables, Integer> createBufferFromAllReturns() {
        BiFunction<List<Double>,Integer,Double> function=(list,idx) -> list.get(idx);
        return createBuffer(function);
    }

    public ReplayBuffer<CartPoleVariables, Integer> createBufferFromStartReturn() {
        BiFunction<List<Double>,Integer,Double> function=(list,idx) -> list.get(0);
        return createBuffer(function);
    }

    public ReplayBuffer<CartPoleVariables, Integer> createBuffer(BiFunction<List<Double>,Integer,Double> function) {
        ReplayBuffer<CartPoleVariables, Integer> bufferEpisodeUpdated=new ReplayBuffer<>(bufferEpisode.size());
        List<Double> returns= createReturnList();
        for (int i = bufferEpisode.size()-1; i >=0 ; i--) {
            Experience<CartPoleVariables, Integer> experience= bufferEpisode.getExperience(i);
            if (isFirstVisitFlagTrueAndIsFirstVisit(i, experience)) {
                double value = function.apply(returns, i);
                addExperience(bufferEpisodeUpdated, experience, value);
            }
        }
        return bufferEpisodeUpdated;
    }

    private boolean isFirstVisitFlagTrueAndIsFirstVisit(int i, Experience<CartPoleVariables, Integer> experience) {
        return isFirstVisit && !bufferEpisode.isExperienceWithStateVariablesPresentBeforeIndex(experience.stateVariables, i);
    }

    private void addExperience(ReplayBuffer<CartPoleVariables, Integer> bufferEpisodeUpdated, Experience<CartPoleVariables, Integer> experience, double value) {
        bufferEpisodeUpdated.addExperience(Experience.<CartPoleVariables, Integer>builder()
                .stateVariables(experience.stateVariables)
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
            Experience<CartPoleVariables, Integer> experience = bufferEpisode.getExperience(i);
            episodeReturn = episodeReturn + discountFactor * experience.reward;
            returns.add(episodeReturn);
        }
        Collections.reverse(returns);
        return returns;
    }

}
