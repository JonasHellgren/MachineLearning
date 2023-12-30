package policy_gradient_problems.abstract_classes;

import policy_gradient_problems.common.TabularValueFunction;

public interface AgentParamActorTabCriticI<V> extends AgentParamActorI<V> {
    TabularValueFunction getCritic();
}
