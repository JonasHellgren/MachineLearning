package multi_step_temp_diff.interfaces_and_abstract;
import multi_step_temp_diff.models.NstepExperience;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import java.util.List;

public interface NetworkMemoryInterface<S>  {
    void save(String fileName);
    void load(String fileName);  //Todo delete
    double read(S state);
    void learn(List<NstepExperience> miniBatch);
    MomentumBackpropagation getLearningRule();
    double getAverageValueError(List<NstepExperience> experienceList);
    void createOutScalers(double minOut, double maxOut);
}
