package maze_domain_design.domain.trainer.value_objects;

import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.environment.Environment;

public record TrainerExternal(
        Environment environment,
        Agent agent
) {
}
