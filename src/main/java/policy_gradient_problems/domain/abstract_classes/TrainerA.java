package policy_gradient_problems.domain.abstract_classes;

import lombok.Getter;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.agent_interfaces.AgentI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.helpers.RecorderActionProbabilities;
import policy_gradient_problems.helpers.RecorderStateValues;
import policy_gradient_problems.helpers.RecorderTrainingProgress;

import java.util.List;

/**
 * The mother of all trainer classes
 * All trainers have a tracker for plotting measures and parameters
 */

@Log
@Getter
public abstract class TrainerA<V> {

    protected RecorderActionProbabilities recorderActionProbabilities=new RecorderActionProbabilities();
    protected RecorderStateValues recorderStateValues=new RecorderStateValues();
    protected RecorderTrainingProgress recorderTrainingProgress =new RecorderTrainingProgress();
    protected TrainerParameters parameters;

    public abstract void train();
    protected abstract List<Experience<V>> getExperiences(AgentI<V> agent);

}
