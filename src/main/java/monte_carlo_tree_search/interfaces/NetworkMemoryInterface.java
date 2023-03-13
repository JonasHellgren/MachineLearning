package monte_carlo_tree_search.interfaces;
import monte_carlo_tree_search.network_training.Experience;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import java.util.List;

public interface NetworkMemoryInterface<SSV, AV>  extends MemoryInterface<SSV> {
    void save(String fileName);
    void load(String fileName);
    double read(StateInterface<SSV> state);
    void learn(List<Experience<SSV, AV>> miniBatch);
    MomentumBackpropagation getLearningRule();
    double getAverageValueError(List<Experience<SSV, AV>> experienceList);
    void createOutScalers(double minOut, double maxOut);

}
