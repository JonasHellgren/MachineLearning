package mcts_cart_pole_runner;

import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.ReplayBuffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public ReplayBuffer<CartPoleVariables, Integer> createBufferDifferentReturns() {
        ReplayBuffer<CartPoleVariables, Integer> bufferEpisodeUpdated=new ReplayBuffer<>(bufferEpisode.size());
        List<Double> returns= createReturnList();
        for (int i = bufferEpisode.size()-1; i >=0 ; i--) {
            Experience<CartPoleVariables, Integer> experience= bufferEpisode.getExperience(i);
            if (isFirstVisitFlagIsTrueAndIsFirstVisit(i, experience)) {
                double value = returns.get(i);  //todo use function interface
                addExperience(bufferEpisodeUpdated, experience, value);
            }
        }
        return bufferEpisodeUpdated;
    }

    private boolean isFirstVisitFlagIsTrueAndIsFirstVisit(int i, Experience<CartPoleVariables, Integer> experience) {
        return isFirstVisit && !bufferEpisode.isExperienceWithStateVariablesPresentBeforeIndex(experience.stateVariables, i);
    }

    public ReplayBuffer<CartPoleVariables, Integer> createBufferSameReturns() {
        ReplayBuffer<CartPoleVariables, Integer> bufferEpisodeUpdated=new ReplayBuffer<>(bufferEpisode.size());
        List<Double> returns= createReturnList();
        for (int i = bufferEpisode.size()-1; i >=0 ; i--) {
            Experience<CartPoleVariables, Integer> experience= bufferEpisode.getExperience(i);
            double value=returns.get(0);
            addExperience(bufferEpisodeUpdated, experience, value);
        }
        return bufferEpisodeUpdated;
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
