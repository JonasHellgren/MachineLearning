package monte_carlo_tree_search.generic_interfaces;

import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.MonteCarloSimulator;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.generic_interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import org.neuroph.nnet.learning.MomentumBackpropagation;


public interface MemoryTrainerInterface<S, A> {

    ReplayBuffer<S,A> createExperienceBuffer(MonteCarloSimulator<S, A> simulator);
    void trainMemory(NetworkMemoryInterface<S, A> memory, ReplayBuffer<S, A> buffer);




}
