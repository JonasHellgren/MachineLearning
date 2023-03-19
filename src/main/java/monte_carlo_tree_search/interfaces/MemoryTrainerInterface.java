package monte_carlo_tree_search.interfaces;

import monte_carlo_tree_search.create_tree.MonteCarloSimulator;
import monte_carlo_tree_search.network_training.ReplayBuffer;


public interface MemoryTrainerInterface<S, A> {

    ReplayBuffer<S,A> createExperienceBuffer(MonteCarloSimulator<S, A> simulator, int bufferSize);
    void trainMemory(NetworkMemoryInterface<S, A> memory, ReplayBuffer<S, A> buffer);

}
