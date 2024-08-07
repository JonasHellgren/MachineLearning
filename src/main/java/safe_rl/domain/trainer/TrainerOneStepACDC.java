package safe_rl.domain.trainer;

import com.joptimizer.exception.JOptimizerException;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.environment.EnvironmentI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.trainer.aggregates.ACDCOneStepEpisodeTrainer;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.simulator.AgentSimulator;
import safe_rl.domain.trainer.aggregates.EpisodeCreator;
import safe_rl.domain.trainer.recorders.Recorder;

import java.util.List;
import java.util.function.Supplier;

//todo TrainerI
@Log
public class TrainerOneStepACDC<V> {

    EnvironmentI<V> environment;
    @Getter AgentACDiscoI<V> agent;
    TrainerParameters trainerParameters;
    Supplier<StateI<V>> startStateSupplier;
    EpisodeCreator<V> episodeCreator;
    ACDCOneStepEpisodeTrainer<V> episodeTrainer;
    @Getter
    Recorder<V> recorder;

    @Builder
    public TrainerOneStepACDC(EnvironmentI<V> environment,
                              AgentACDiscoI<V> agent,
                              SafetyLayer<V> safetyLayer,
                              TrainerParameters trainerParameters,
                              Supplier<StateI<V>> startStateSupplier) {
        this.environment = environment;
        this.agent = agent;
        this.trainerParameters = trainerParameters;
        this.episodeCreator = EpisodeCreator.<V>builder()
                .environment(environment).safetyLayer(safetyLayer).parameters(trainerParameters)
                .build();
        this.startStateSupplier=startStateSupplier;
        this.episodeTrainer = ACDCOneStepEpisodeTrainer
                .<V>builder()
                .agent(agent).parameters(trainerParameters)
                .build();
        AgentSimulator<V> simulator = new AgentSimulator<>(
                agent, safetyLayer, startStateSupplier, environment);
        recorder=new Recorder<>(simulator,trainerParameters);
    }

    public void train() throws JOptimizerException {
        int bound = trainerParameters.nofEpisodes();
        for (int i = 0; i < bound; i++) {
            processEpisode();
        }
    }

    public List<Experience<V>> evaluate() throws JOptimizerException {
        return episodeCreator.getExperiences(agent,startStateSupplier.get());
    }

    private void processEpisode() throws JOptimizerException {
        var experiences = evaluate();
        var errorList= recorder.criticLossTraj();
        episodeTrainer.trainAgentFromExperiences(experiences,errorList);
        recorder.recordTrainingProgress(experiences,agent);
    }

}
