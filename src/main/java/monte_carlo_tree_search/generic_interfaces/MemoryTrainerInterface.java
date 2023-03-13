package monte_carlo_tree_search.generic_interfaces;

import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.generic_interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import org.neuroph.nnet.learning.MomentumBackpropagation;


public interface MemoryTrainerInterface<SSV, AV> {

    ReplayBuffer<SSV,AV> createExperienceBuffer(MonteCarloTreeCreator<SSV, AV> monteCarloTreeCreator);
    void trainMemory(NetworkMemoryInterface<SSV, AV> memory, ReplayBuffer<SSV, AV> buffer);




}
