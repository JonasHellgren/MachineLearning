package mcts_cart_pole_runner;

import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.ReplayBuffer;

public class ReplayBufferValueSetter {

    ReplayBuffer<CartPoleVariables, Integer> bufferEpisode;
    double discountFactor;
    double episodeReturn;

    public ReplayBufferValueSetter(ReplayBuffer<CartPoleVariables, Integer> bufferEpisode, double discountFactor) {
        this.bufferEpisode = bufferEpisode;
        this.discountFactor = discountFactor;

    }

    public double getEpisodeReturn() {
        return episodeReturn;
    }

    public ReplayBuffer<CartPoleVariables, Integer> createBuffer() {
        ReplayBuffer<CartPoleVariables, Integer> bufferEpisodeUpdated=new ReplayBuffer<>(bufferEpisode.size());
        this.episodeReturn=0;
        for (int i = bufferEpisode.size()-1; i >=0 ; i--) {
            Experience<CartPoleVariables, Integer> experience= bufferEpisode.getExperience(i);
            episodeReturn=episodeReturn+ discountFactor *experience.reward;
            bufferEpisodeUpdated.addExperience(Experience.<CartPoleVariables, Integer>builder()
                    .stateVariables(experience.stateVariables)
                    .value(episodeReturn)
                    .reward(experience.reward)
                    .build());
        }
        return bufferEpisodeUpdated;
    }

}
