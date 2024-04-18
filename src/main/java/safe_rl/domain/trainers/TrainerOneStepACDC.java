package safe_rl.domain.trainers;

import lombok.Builder;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.abstract_classes.EnvironmentI;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.episode_trainers.ACDCOneStepEpisodeTrainer;
import safe_rl.domain.safety_layer.SafetyLayerI;
import safe_rl.domain.value_classes.TrainerParameters;

//todo TrainerI
public class TrainerOneStepACDC<V> {

    EnvironmentI<V> environment;
    AgentACDiscoI<V> agent;
    TrainerParameters trainerParameters;
    StateI<V> startState;
    ExperienceCreator<V> experienceCreator;
    //Recorders recorders;


    @Builder
    public TrainerOneStepACDC(EnvironmentI<V> environment,
                              AgentACDiscoI<V> agent,
                              SafetyLayerI<V> safetyLayer,
                              TrainerParameters trainerParameters) {
        this.environment = environment;
        this.agent = agent;
        this.trainerParameters = trainerParameters;
        this.experienceCreator= ExperienceCreator.<V>builder()
                .environment(environment).safetyLayer(safetyLayer).parameters(trainerParameters)
                .build();
    }

    public void train() {
        var episodeTrainer = ACDCOneStepEpisodeTrainer
                .<V>builder()
                .agent(agent).parameters(trainerParameters)
                .build();

        for (int ei = 0; ei < trainerParameters.nofEpisodes(); ei++) {
            agent.setState(startState.copy());
            episodeTrainer.trainAgentFromExperiences(experienceCreator.getExperiences(agent));
        }
    }


    //updateRecorders((s) -> agent.getHelper().calcActionProbsInObsState(s),lossActorAndCritic);



}
