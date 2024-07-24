package domain_design_tabular_q_learning.domain.agent;

import lombok.Getter;
import domain_design_tabular_q_learning.domain.agent.aggregates.Memory;
import domain_design_tabular_q_learning.domain.agent.helpers.BestActionSelector;
import domain_design_tabular_q_learning.domain.agent.value_objects.AgentProperties;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.environment.value_objects.Action;
import org.apache.commons.lang3.RandomUtils;


@Getter
public class Agent<S> {
    Memory<S> memory;
    AgentProperties properties;
    BestActionSelector<S> actionSelector;

    public Agent(AgentProperties properties, EnvironmentI<S> environment) {
        this.memory=Memory.withProperties(properties);
        this.properties=properties;
        this.actionSelector=new BestActionSelector<>(properties,environment,memory);
    }

    public Action chooseAction(StateI<S> s, double probRandom) {
        if (RandomUtils.nextDouble(0, 1) < probRandom) {
            return Action.random();
        }
        return actionSelector.chooseBestAction(s);
    }
}
