package monte_carlo_tree_search.generic_interfaces;

import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import java.util.List;

public interface NetworkMemoryInterface<SSV> extends MemoryInterface<SSV> {
    void save(String fileName);
    void load(String fileName);
    void learn(List<Experience<SSV, Integer>> miniBatch);
    MomentumBackpropagation getLearningRule();
    double getAverageValueError(List<Experience<SSV, Integer>> experienceList);
    void createOutScalers(int maxNofSteps);

}
