package safe_rl.domain.trainer;

import com.joptimizer.exception.JOptimizerException;
import lombok.Builder;
import lombok.extern.java.Log;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.environment.EnvironmentI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.trainer.mediators.MediatorSingleStep;
import safe_rl.domain.trainer.mediators.MediatorSingleStepI;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.TrainerExternal;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.simulator.AgentSimulator;
import safe_rl.domain.trainer.recorders.Recorder;
import safe_rl.environments.trading_electricity.StateTrading;

import java.util.List;
import java.util.function.Supplier;

@Log
public class TrainerOneStepACDC<V> {

    MediatorSingleStepI<V> mediator;

    @Builder
    public TrainerOneStepACDC(EnvironmentI<V> environment,
                              AgentACDiscoI<V> agent,
                              SafetyLayer<V> safetyLayer,
                              TrainerParameters trainerParameters,
                              Supplier<StateI<V>> startStateSupplier) {

        var external = new TrainerExternal<>(
                environment, agent, safetyLayer, startStateSupplier);
        var simulator = AgentSimulator.ofExternal(external);
        var recorder = new Recorder<>(simulator,trainerParameters);
        this.mediator=new MediatorSingleStep<>(
                external,
                trainerParameters,recorder, StateTrading.INDEX_SOC);
    }

    public Recorder<V> getRecorder() {
        return mediator.getRecorder();
    }

    public List<Experience<V>> getExperiences() throws JOptimizerException {
        return mediator.getExperiences();
    }

    public   AgentACDiscoI<V> getAgent() {
        return mediator.getExternal().agent();
    }


    public void train() throws JOptimizerException {
        var trainerParameters=mediator.getParameters();
        int bound = trainerParameters.nofEpisodes();
        for (int i = 0; i < bound; i++) {
            var experiences=mediator.getExperiences();
            mediator.fitAgentFromNewExperiences(experiences);
            mediator.updateRecorder(experiences);
        }
    }


}
