package policy_gradient_problems.domain.agent_interfaces;

import policy_gradient_problems.domain.abstract_classes.StateI;

import java.util.List;

public interface NeuralActorI<V> {
    void fitActor(List<List<Double>> inList, List<List<Double>> outList);
    List<Double> actorOut(StateI<V> state);
}
