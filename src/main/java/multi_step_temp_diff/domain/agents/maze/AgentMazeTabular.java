package multi_step_temp_diff.domain.agents.maze;

import multi_step_temp_diff.domain.agent_abstract.AgentAbstract;
import multi_step_temp_diff.domain.agent_abstract.AgentTabularInterface;
import multi_step_temp_diff.domain.agent_valueobj.AgentMazeTabularSettings;
import multi_step_temp_diff.domain.agent_valueobj.AgentSettingsInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environments.maze.MazeVariables;
import multi_step_temp_diff.domain.environments.maze.MazeState;
import java.util.Map;

public class AgentMazeTabular extends AgentAbstract<MazeVariables> implements AgentTabularInterface<MazeVariables> {

    AgentMazeTabularSettings settings;
    Map<MazeState, Double> memory;

    public static AgentMazeTabular newDefault(EnvironmentInterface<MazeVariables> environment) {
        return new AgentMazeTabular(environment,AgentMazeTabularSettings.getDefault());
    }

    public static AgentMazeTabular newFromSettings(EnvironmentInterface<MazeVariables> environment,
                                            AgentMazeTabularSettings settings) {
        return new AgentMazeTabular(environment,settings);
    }

    private AgentMazeTabular(EnvironmentInterface<MazeVariables> environment,
                             AgentMazeTabularSettings settings) {
        super(environment, new MazeState(MazeVariables.newFromXY(settings.startX(),settings.startY())),settings);
        this.settings=AgentMazeTabularSettings.getDefault();
        this.memory = settings.memory();
    }

    @Override
    public double readValue(StateInterface<MazeVariables>  state) {
        return memory.getOrDefault((MazeState) state, settings.valueNotPresent());
    }

    @Override
    public void writeValue(StateInterface<MazeVariables>  state, double value) {
        memory.put((MazeState) state, value);
    }



    public void clear() {
        super.clear();
        memory.clear();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
