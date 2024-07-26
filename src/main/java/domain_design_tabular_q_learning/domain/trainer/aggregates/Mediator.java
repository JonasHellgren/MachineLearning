package domain_design_tabular_q_learning.domain.trainer.aggregates;

import lombok.Getter;
import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.trainer.entities.Experience;
import domain_design_tabular_q_learning.domain.trainer.entities.Recording;
import domain_design_tabular_q_learning.domain.trainer.value_objects.TrainerExternal;
import domain_design_tabular_q_learning.domain.trainer.value_objects.TrainerProperties;
import java.util.stream.IntStream;

public class Mediator<V,A,P> implements MediatorI<V,A,P> {
    @Getter
    TrainerExternal<V,A,P> external;
    @Getter
    TrainerProperties properties;
    @Getter
    Recorder recorder;
    EpisodeCreator<V,A,P> episodeCreator;
    AgentFitter<V,A,P> fitter;

    public Mediator(EnvironmentI<V,A,P> environment,
                    Agent<V,A,P> agent,
                    TrainerProperties properties) {
        this.external = new TrainerExternal<>(environment, agent);
        this.properties = properties;
        this.recorder = new Recorder();
        this.episodeCreator = new EpisodeCreator<>(this);
        this.fitter = new AgentFitter<>(this);
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
    public Episode<V,A> runEpisode() {
        return episodeCreator.runEpisode();
    }

    @Override
    public double pRandomAction() {
        double trainingProgress = getRecorder().size() / (double) properties.nEpisodes();
        return external.agent().getProperties().probRandomAction(trainingProgress);
    }

    @Override
    public double fitAgentMemoryFromExperience(Experience<V,A> e) {
        return fitter.fitAgentFromExperience(e);
    }

}
