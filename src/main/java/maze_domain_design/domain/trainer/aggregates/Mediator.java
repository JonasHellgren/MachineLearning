package maze_domain_design.domain.trainer.aggregates;

import lombok.Getter;
import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.environment.EnvironmentI;
import maze_domain_design.domain.trainer.entities.Experience;
import maze_domain_design.domain.trainer.entities.Recording;
import maze_domain_design.domain.trainer.value_objects.TrainerExternal;
import maze_domain_design.domain.trainer.value_objects.TrainerProperties;
import java.util.stream.IntStream;

public class Mediator<V> implements MediatorI<V> {
    @Getter
    TrainerExternal<V> external;
    @Getter
    TrainerProperties properties;
    @Getter
    Recorder recorder;
    EpisodeCreator<V> episodeCreator;
    AgentFitter fitter;

    public Mediator(EnvironmentI<V> environment,
                    Agent<V> agent,
                    TrainerProperties properties) {
        this.external = new TrainerExternal<>(environment, agent);
        this.properties = properties;
        this.recorder = new Recorder();
        this.episodeCreator = new EpisodeCreator<>(this);
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
    public double pRandomAction() {
        double trainingProgress = getRecorder().size() / (double) properties.nEpisodes();
        return external.agent().getProperties().probRandomAction(trainingProgress);
    }

    @Override
    public double fitAgentMemoryFromExperience(Experience<V> e) {
        return fitter.fitAgentFromExperience(e);
    }

}
