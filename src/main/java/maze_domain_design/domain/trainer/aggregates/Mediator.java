package maze_domain_design.domain.trainer.aggregates;

import lombok.Getter;
import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.environment.Environment;
import maze_domain_design.domain.environment.value_objects.State;
import maze_domain_design.domain.trainer.value_objects.StartStateSupplier;
import maze_domain_design.domain.trainer.value_objects.TrainerExternal;
import maze_domain_design.domain.trainer.value_objects.TrainerProperties;

public class Mediator implements MediatorI {
    @Getter TrainerExternal external;
    @Getter TrainerProperties properties;
    @Getter Recorder recorder;
    StartStateSupplier startStateSupplier;
    EpisodeCreator episodeCreator;
    AgentFitter fitter;
    RecorderUpdater recUpdater;

    public Mediator(Environment environment,
                    Agent agent,
                    TrainerProperties properties) {
        this.external = new TrainerExternal(environment, agent);
        this.properties = properties;
        this.startStateSupplier=new StartStateSupplier(
                properties,environment.getProperties());
        this.episodeCreator=new EpisodeCreator();
        episodeCreator.setMediator(this);
    }

    @Override
    public void train() {
        var episode=createEpisode();
    }

    @Override
    public Episode createEpisode() {
        return episodeCreator.runEpisode();
    }

    @Override
    public State getStartState() {
        return startStateSupplier.getStartState();
    }

    @Override
    public void fitAgentMemoryFromEpisode(Episode episode) {

    }

    @Override
    public void updateRecorder() {

    }

}
