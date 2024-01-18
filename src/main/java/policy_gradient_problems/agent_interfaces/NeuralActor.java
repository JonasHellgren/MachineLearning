package policy_gradient_problems.agent_interfaces;

import org.apache.commons.math3.util.Pair;

import java.util.List;

public interface NeuralActor<V> {
    void fitActorOld(List<Double> in, List<Double> out);  //todo remove

    void fitActor(List<List<Double>> inList, List<List<Double>> outList);
}
