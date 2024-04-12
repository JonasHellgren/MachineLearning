package policy_gradient_problems.domain.agent_interfaces;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.domain.abstract_classes.StateI;

import java.util.List;

public interface NeuralActorI<V> {
    void fitActor(List<List<Double>> inList, List<List<Double>> outList);
    List<Double> actorOut(StateI<V> state);

    /**
     *  Relevant for cont action agents
     */
    default Pair<Double,Double> meanAndStd(StateI<V> state) {
        List<Double> list=actorOut(state);
        Preconditions.checkArgument(list.size()==2,"Non correct list size actor out");
        return Pair.create(list.get(0), list.get(1));
    }
}
