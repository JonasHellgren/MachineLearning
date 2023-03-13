package monte_carlo_tree_search.generic_interfaces;
import monte_carlo_tree_search.network_training.Experience;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import java.util.List;

public interface NetworkMemoryInterface<SSV>  extends MemoryInterface<SSV> {
    void save(String fileName);
    void load(String fileName);
    double read(StateInterface<SSV> state);
    void learn(List<Experience<SSV, Integer>> miniBatch);
    MomentumBackpropagation getLearningRule();
    double getAverageValueError(List<Experience<SSV, Integer>> experienceList);
    void createOutScalers(double minOut, double maxOut);

}
