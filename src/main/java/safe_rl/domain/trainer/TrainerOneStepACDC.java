package safe_rl.domain.trainer;

import com.joptimizer.exception.JOptimizerException;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import safe_rl.domain.agent.AgentACDCSafe;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.environment.EnvironmentI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.trainer.aggregates.ACDCOneStepEpisodeTrainer;
import safe_rl.domain.trainer.mediators.MediatorBaseI;
import safe_rl.domain.trainer.mediators.MediatorMultiStep;
import safe_rl.domain.trainer.mediators.MediatorSingleStep;
import safe_rl.domain.trainer.mediators.MediatorSingleStepI;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.TrainerExternal;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.simulator.AgentSimulator;
import safe_rl.domain.trainer.aggregates.EpisodeCreator;
import safe_rl.domain.trainer.recorders.Recorder;
import safe_rl.environments.trading_electricity.StateTrading;

import java.util.List;
import java.util.function.Supplier;

//todo TrainerI
@Log
public class TrainerOneStepACDC<V> {

    MediatorSingleStepI<V> mediator;

/*
    EnvironmentI<V> environment;
    @Getter AgentACDiscoI<V> agent;
    TrainerParameters trainerParameters;
    Supplier<StateI<V>> startStateSupplier;
    EpisodeCreator<V> episodeCreator;
    ACDCOneStepEpisodeTrainer<V> episodeTrainer;
    @Getter
    Recorder<V> recorder;
*/

    @Builder
    public TrainerOneStepACDC(EnvironmentI<V> environment,
                              AgentACDiscoI<V> agent,
                              SafetyLayer<V> safetyLayer,
                              TrainerParameters trainerParameters,
                              Supplier<StateI<V>> startStateSupplier) {
/*        this.environment = environment;
        this.agent = agent;
        this.trainerParameters = trainerParameters;
*//*        this.episodeCreator = EpisodeCreator.<V>builder()
                .environment(environment).safetyLayer(safetyLayer).parameters(trainerParameters)
                .build();*//*
        this.episodeCreator=new EpisodeCreator<>(null);  //TODO FIX
        this.startStateSupplier=startStateSupplier;
        this.episodeTrainer = ACDCOneStepEpisodeTrainer
                .<V>builder()
                .agent(agent).parameters(trainerParameters)
                .build();
        AgentSimulator<V> simulator = new AgentSimulator<>(
                agent, safetyLayer, startStateSupplier, environment);
        recorder=new Recorder<>(simulator,trainerParameters);*/

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

/*public AgentACDCSafe getAgent() {
        return mediator.getExternal().agent();
}*/


    public void train() throws JOptimizerException {
        var trainerParameters=mediator.getParameters();
        var recorder=mediator.getRecorder();

        int bound = trainerParameters.nofEpisodes();
        for (int i = 0; i < bound; i++) {
            //var experiences = getExperiences();
            //var errorList= recorder.criticLossTraj();
            //episodeTrainer.trainAgentFromExperiences(experiences,errorList);
            var experiences=mediator.getExperiences();
            mediator.fitAgentFromNewExperiences(experiences);
            mediator.updateRecorder(experiences);
//            recorder.recordTrainingProgress(experiences,agent);
        }
    }

/*
    public List<Experience<V>> getExperiences() throws JOptimizerException {
        return episodeCreator.getExperiences(agent,startStateSupplier.get());
    }
*/

/*    private void processEpisode() throws JOptimizerException {
        var experiences = evaluate();
        var errorList= recorder.criticLossTraj();
        episodeTrainer.trainAgentFromExperiences(experiences,errorList);
        recorder.recordTrainingProgress(experiences,agent);
    }*/

}
