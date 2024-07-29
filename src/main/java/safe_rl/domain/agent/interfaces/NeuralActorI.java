package safe_rl.domain.agent.interfaces;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.domain.abstract_classes.StateI;

import java.util.List;

import static common.dl4j.LossPPO.MEAN_CONT_INDEX;
import static common.dl4j.LossPPO.STD_CONT_INDEX;

public interface NeuralActorI<V> {
    void fitActor(List<List<Double>> inList, List<List<Double>> outList);
    List<Double> actorOut(StateI<V> state);

    /**
     *  Relevant for cont action agents
     */
    default Pair<Double,Double> meanAndStd(StateI<V> state) {
        List<Double> list=actorOut(state);
        Preconditions.checkArgument(list.size()==2,"Non correct list size actor out");
        return Pair.create(list.get(MEAN_CONT_INDEX), list.get(STD_CONT_INDEX));
    }
}
