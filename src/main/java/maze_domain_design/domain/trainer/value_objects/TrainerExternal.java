package maze_domain_design.domain.trainer.value_objects;

import maze_domain_design.domain.agent.Agent;
import maze_domain_design.environments.obstacle_on_road.EnvironmentRoad;

public record TrainerExternal(
        EnvironmentRoad environment,
        Agent agent
) {
}
