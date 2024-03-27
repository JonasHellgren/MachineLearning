package policy_gradient_problems.domain.abstract_classes;

import lombok.NonNull;
import policy_gradient_problems.domain.agent_interfaces.NeuralActor;
import policy_gradient_problems.domain.value_classes.MultiStepResults;

public interface ActorUpdaterI<V> {

    void updateActor(MultiStepResults msRes, NeuralActor<V> agent);

}
