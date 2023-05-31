package monte_carlo_tree_search.interfaces;
import monte_carlo_tree_search.network_training.Experience;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import java.util.List;

public interface NetworkMemoryInterface<S, A>  extends ReadableMemoryInterface<S> {
    void save(String fileName);
    void load(String fileName);
    double read(StateInterface<S> state);
    void learn(List<Experience<S, A>> miniBatch);
    MomentumBackpropagation getLearningRule();
    double getAverageValueError(List<Experience<S, A>> experienceList);
    void createOutScalers(double minOut, double maxOut);
}
