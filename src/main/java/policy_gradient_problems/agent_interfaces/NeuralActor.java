package policy_gradient_problems.agent_interfaces;

import java.util.List;

public interface NeuralActor<V> {
    void fitActorOld(List<Double> in, List<Double> out);  //todo remove

    void fitActor(List<List<Double>> inList, List<List<Double>> outList);
}
