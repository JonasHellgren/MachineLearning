package multi_step_temp_diff.interfaces_and_abstract;
import multi_step_temp_diff.models.NstepExperience;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import java.util.List;

public interface NetworkMemoryInterface<S>  {
    double read(StateInterface<S> state);
    void learn(List<NstepExperience<S>> miniBatch);
}
