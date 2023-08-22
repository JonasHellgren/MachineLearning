package multi_step_temp_diff.domain.agents.fork;

import lombok.Builder;
import lombok.Getter;
import multi_step_temp_diff.domain.agent_abstract.AgentAbstract;
import multi_step_temp_diff.domain.agent_abstract.AgentTabularInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;

import java.util.*;

@Getter
public class AgentForkTabular extends AgentAbstract<ForkVariables> implements AgentTabularInterface<ForkVariables> {

    AgentForkTabularSettings settings;
    Map<Integer, Double> memory;

    public static AgentForkTabular newDefault(EnvironmentInterface<ForkVariables> environment) {
        var settings= AgentForkTabularSettings.getDefault();
        return  newWithStartState(environment,new ForkState(ForkVariables.newFromPos(settings.startState())));
    }

    public static AgentForkTabular newWithStartState(EnvironmentInterface<ForkVariables> environment,
                                                     StateInterface<ForkVariables>  startState) {
        return AgentForkTabular.builder()
                .environment(environment).state(startState)
                .settings(AgentForkTabularSettings.getDefault())
                .build();
    }

    @Builder
    private AgentForkTabular(EnvironmentInterface<ForkVariables> environment,
                             StateInterface<ForkVariables> state,
                             AgentForkTabularSettings settings) {
        super(environment,state, settings);
        this.settings=settings;
        this.memory = settings.memory();
    }

    @Override
    public double readValue(StateInterface<ForkVariables>  state) {
        return memory.getOrDefault(state.getVariables().position, settings.valueNotPresent());
    }

    @Override
    public void writeValue(StateInterface<ForkVariables>  state, double value) {
        memory.put(state.getVariables().position, value);
    }

    public void clear() {
        super.clear();
        memory.clear();
    }


}
