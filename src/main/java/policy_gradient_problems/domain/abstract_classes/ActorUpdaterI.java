package policy_gradient_problems.domain.abstract_classes;

import policy_gradient_problems.domain.agent_interfaces.NeuralActorI;
import policy_gradient_problems.domain.value_classes.MultiStepResults;

public interface ActorUpdaterI<V> {

    void updateActor(MultiStepResults msRes, NeuralActorI<V> agent);

}
