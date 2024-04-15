package policy_gradient_problems.domain.agent_interfaces;

import org.apache.commons.math3.util.Pair;

public interface AgentParamActorI<V> extends AgentI<V>, ParamActorI<V> {
    Pair<Double,Double> meanAndStd(int state);
}
