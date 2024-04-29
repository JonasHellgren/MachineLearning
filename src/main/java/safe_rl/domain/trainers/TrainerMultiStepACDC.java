package safe_rl.domain.trainers;

import com.joptimizer.exception.JOptimizerException;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.abstract_classes.*;
import safe_rl.domain.episode_trainers.ACDCMultiStepEpisodeTrainer;
import safe_rl.domain.memories.ReplayBufferMultiStepExp;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.ExperienceMultiStep;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.helpers.AgentSimulator;
import safe_rl.helpers.ExperienceCreator;
import safe_rl.recorders.Recorders;

import java.util.List;
import java.util.function.Supplier;

@Log
public class TrainerMultiStepACDC<V> {
    EnvironmentI<V> environment;
    @Getter AgentACDiscoI<V> agent;
    TrainerParameters trainerParameters;
    Supplier<StateI<V>> startStateSupplier;
    ExperienceCreator<V> experienceCreator;
    ACDCMultiStepEpisodeTrainer<V> episodeTrainer;
    ReplayBufferMultiStepExp<V> buffer;
    CriticFitterUsingReplayBuffer<V> fitter;

    @Getter Recorders<V> recorder;

    @Builder
    public TrainerMultiStepACDC(EnvironmentI<V> environment,
                              AgentACDiscoI<V> agent,
                              SafetyLayer<V> safetyLayer,
                              TrainerParameters trainerParameters,
                              Supplier<StateI<V>> startStateSupplier) {
        this.environment = environment;
        this.agent = agent;
        this.trainerParameters = trainerParameters;
        this.experienceCreator= ExperienceCreator.<V>builder()
                .environment(environment).safetyLayer(safetyLayer).parameters(trainerParameters)
                .build();
        this.startStateSupplier=startStateSupplier;
        this.episodeTrainer = new ACDCMultiStepEpisodeTrainer<>(agent,trainerParameters);
        buffer = ReplayBufferMultiStepExp.newFromMaxSize(trainerParameters.replayBufferSize());
        this.fitter= new CriticFitterUsingReplayBuffer<>(agent.getCritic(), trainerParameters);

        recorder=new Recorders<>(new AgentSimulator<>(
                agent,safetyLayer,startStateSupplier,environment));
    }

    public void train() throws JOptimizerException {
        for (int i = 0; i <  trainerParameters.nofEpisodes(); i++) {
            var experiences = experienceCreator.getExperiences(agent,startStateSupplier.get());
            processEpisode(experiences);
/*              fitter.fit(buffer);

          for (Experience experience:experiences) {
                var expMs= ExperienceMultiStep.<V>builder()
                        .state(experience.state())
                        .a
            }
            */


        }
    }

    private void processEpisode(List<Experience<V>> experiences) throws JOptimizerException {
        var errorList= recorder.recorderTrainingProgress.criticLossTraj();
        episodeTrainer.trainAgentFromExperiences(experiences,errorList);
        recorder.recordTrainingProgress(experiences,agent);
    }

}
