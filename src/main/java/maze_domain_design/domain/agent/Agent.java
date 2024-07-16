package maze_domain_design.domain.agent;

import lombok.Getter;
import maze_domain_design.domain.agent.aggregates.Memory;
import maze_domain_design.domain.agent.helpers.BestActionSelector;
import maze_domain_design.domain.agent.value_objects.AgentProperties;
import maze_domain_design.domain.environment.Environment;
import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.environment.value_objects.State;
import org.apache.commons.lang3.RandomUtils;



public class Agent {
    @Getter Memory memory;
    BestActionSelector actionSelector;

    public Agent(AgentProperties properties, Environment environment) {
        this.memory=Memory.withProperties(properties);
        this.actionSelector=new BestActionSelector(properties,environment,memory);
    }

    public Action chooseAction(State s, double probRandom) {
        if (RandomUtils.nextDouble(0, 1) < probRandom) {
            return Action.random();
        }
        return actionSelector.chooseBestAction(s);
    }
}