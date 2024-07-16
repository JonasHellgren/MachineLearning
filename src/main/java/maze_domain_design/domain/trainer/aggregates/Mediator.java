package maze_domain_design.domain.trainer.aggregates;

import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.environment.Environment;
import maze_domain_design.domain.trainer.value_objects.TrainerExternal;
import maze_domain_design.domain.trainer.value_objects.TrainerProperties;

public class Mediator {
    TrainerExternal external;
    TrainerProperties properties;
    Recorder recorder;
    EpisodeCreator episodeCreator;
    AgentFitter fitter;
    RecorderUpdater recUpdater;

    public Mediator(Environment environment,
                    Agent agent,
                    TrainerProperties properties) {
        this.external=new TrainerExternal(environment,agent);
        this.properties = properties;
    }
}
