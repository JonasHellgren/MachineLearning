package safe_rl.agent_interfaces;

import common.math.SafeGradientClipper;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.memories.DisCoMemory;

/**
 * fitActor returns gradLogOfMeanAndStd, output not intended to use but good to have
 */

public interface DiscoActorI<V> {
    DisCoMemory<V> getActorMean();
    DisCoMemory<V> getActorLogStd();
    SafeGradientClipper getMeanGradClipper();
    SafeGradientClipper getStdGradClipper();
    Pair<Double,Double> fitActor(StateI<V> state,Action action, double adv);
    Pair<Double,Double> readActor(StateI<V> state);
    Pair<Double, Double> gradientMeanAndStd(StateI<V> state, Action action);
    double lossActorLastUpdates();
    void clearActorLosses();
    double entropy(StateI<V> state);
}
