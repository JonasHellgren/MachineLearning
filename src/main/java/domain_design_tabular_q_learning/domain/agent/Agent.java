package domain_design_tabular_q_learning.domain.agent;

import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import lombok.Getter;
import domain_design_tabular_q_learning.domain.agent.aggregates.Memory;
import domain_design_tabular_q_learning.domain.agent.helpers.BestActionSelector;
import domain_design_tabular_q_learning.domain.agent.value_objects.AgentProperties;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import org.apache.commons.lang3.RandomUtils;


@Getter
public class Agent<S,A> {
    Memory<S,A> memory;
    AgentProperties properties;
    EnvironmentI<S,A> environment;
    BestActionSelector<S,A> actionSelector;

    public Agent(AgentProperties properties, EnvironmentI<S,A> environment) {
        this.memory=Memory.of(properties,environment.actions());
        this.properties=properties;
        this.environment=environment;
        this.actionSelector=new BestActionSelector<>(properties,environment,memory);
    }

    public ActionI<A> chooseAction(StateI<S> s, double probRandom) {
        if (RandomUtils.nextDouble(0, 1) < probRandom) {
            return environment.randomAction();
        }
        return actionSelector.chooseBestAction(s);
    }
}
