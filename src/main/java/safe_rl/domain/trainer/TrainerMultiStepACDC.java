package safe_rl.domain.trainer;

import com.joptimizer.exception.JOptimizerException;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.environment.EnvironmentI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.trainer.aggregates.*;
import safe_rl.domain.trainer.value_objects.TrainerExternal;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.domain.simulator.AgentSimulator;
import safe_rl.domain.trainer.recorders.Recorder;

import java.util.function.Supplier;


/**
 * Doing agent training
 */

@Log
public class TrainerMultiStepACDC<V> {
    Mediator<V> mediator;

    EnvironmentI<V> environment;
    @Getter
    AgentACDiscoI<V> agent;

    @Builder
    public TrainerMultiStepACDC(EnvironmentI<V> environment,
                                AgentACDiscoI<V> agent,
                                SafetyLayer<V> safetyLayer,
                                TrainerParameters trainerParameters,
                                Supplier<StateI<V>> startStateSupplier) {
        this.environment = environment;
        this.agent = agent;
        var external = new TrainerExternal<>(environment, agent, safetyLayer, startStateSupplier);
        var simulator = AgentSimulator.ofExternal(external);
        var recorder = new Recorder<>(simulator,trainerParameters);
        this.mediator=new Mediator<>(
                external,
                trainerParameters,recorder,StateTrading.INDEX_SOC);
    }

    public  Recorder<V> getRecorder() {
        return mediator.getRecorder();
    }

    public void train() throws JOptimizerException {
        ReplayBufferMultiStepExp<V> buffer = createReplayBuffer(mediator.getParameters());
        for (int i = 0; i < mediator.getParameters().nofEpisodes(); i++) {
            var experiences = mediator.getExperiences();
            var msr = mediator.fitAgentFromNewExperiences(experiences);
            mediator.addNewExperiencesToBuffer(msr, buffer);
            mediator.fitAgentFromOldExperiences(buffer);
            mediator.updateRecorder(experiences);
        }
    }

    private static <V> ReplayBufferMultiStepExp<V> createReplayBuffer(TrainerParameters parameters) {
        return ReplayBufferMultiStepExp.newFromSizeAndIsRemoveOldest(
                parameters.replayBufferSize(), parameters.isRemoveOldest());
    }




}
