package safe_rl.agent_interfaces;

import org.apache.commons.math3.util.Pair;
import safe_rl.domain.abstract_classes.Action;

public interface DiscoActorI<V> {
    void fitActor(Action action, double adv);
    Pair<Double,Double> readActor();
}
