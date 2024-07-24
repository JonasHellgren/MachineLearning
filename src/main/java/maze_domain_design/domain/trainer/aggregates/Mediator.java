package maze_domain_design.domain.trainer.aggregates;

import lombok.Getter;
import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.environment.Environment;
import maze_domain_design.environments.obstacle_on_road.StateRoad;
import maze_domain_design.domain.trainer.entities.Experience;
import maze_domain_design.domain.trainer.entities.Recording;
import maze_domain_design.domain.trainer.value_objects.StartStateSupplier;
import maze_domain_design.domain.trainer.value_objects.TrainerExternal;
import maze_domain_design.domain.trainer.value_objects.TrainerProperties;
import java.util.stream.IntStream;

public class Mediator implements MediatorI {
    @Getter
    TrainerExternal external;
    @Getter
    TrainerProperties properties;
    @Getter
    Recorder recorder;
    StartStateSupplier startStateSupplier;
    EpisodeCreator episodeCreator;
    AgentFitter fitter;

    public Mediator(Environment environment,
                    Agent agent,
                    TrainerProperties properties) {
        this.external = new TrainerExternal(environment, agent);
        this.properties = properties;
        this.recorder = new Recorder();
        this.startStateSupplier = new StartStateSupplier(
                properties, environment.getProperties());
        this.episodeCreator = new EpisodeCreator(this);
        this.fitter = new AgentFitter(this);
    }

    @Override
    public void train() {
        recorder.clear();
        IntStream.range(0, properties.nEpisodes()).forEach(ei -> {
            var episode = runEpisode();
            recorder.addRecording(Recording.ofIdAndEpisode(ei, episode));
        });
    }

    @Override
    public Episode runEpisode() {
        return episodeCreator.runEpisode();
    }

    @Override
    public StateRoad getStartState() {
        return startStateSupplier.getStartState();
    }

    @Override
    public double pRandomAction() {
        double trainingProgress = getRecorder().size() / (double) properties.nEpisodes();
        return external.agent().getProperties().probRandomAction(trainingProgress);
    }

    @Override
    public double fitAgentMemoryFromExperience(Experience e) {
        return fitter.fitAgentFromExperience(e);
    }

}
