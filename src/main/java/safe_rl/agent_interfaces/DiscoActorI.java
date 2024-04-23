package safe_rl.agent_interfaces;

import org.apache.commons.math3.util.Pair;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;

/**
 * fitActor returns gradLogOfMeanAndStd, output not intended to use but good to have
 */

public interface DiscoActorI<V> {
    Pair<Double,Double> fitActor(StateI<V> state,Action action, double adv);
    Pair<Double,Double> readActor(StateI<V> state);
    double lossActorLastUpdates();
    void clearActorLosses();
    double entropy(StateI<V> state);
}
