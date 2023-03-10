package monte_carlo_tree_search.network_training;

import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.generic_interfaces.NetworkMemoryInterface;

public interface MemoryTrainerHelperInterface<SSV, AV> {

    ReplayBuffer<SSV,AV> createExperienceBuffer(MonteCarloTreeCreator<SSV, AV> monteCarloTreeCreator);
    NetworkMemoryInterface<SSV>  createMemory(ReplayBuffer<SSV, AV> buffer);

}
