package safe_rl.domain.trainers;

import lombok.Builder;
import lombok.Getter;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.abstract_classes.EnvironmentI;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.episode_trainers.ACDCOneStepEpisodeTrainer;
import safe_rl.domain.safety_layer.SafetyLayerI;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.TrainerParameters;

import java.util.List;

//todo TrainerI
public class TrainerOneStepACDC<V> {

    EnvironmentI<V> environment;
    @Getter
    AgentACDiscoI<V> agent;
    TrainerParameters trainerParameters;
    StateI<V> startState;
    ExperienceCreator<V> experienceCreator;
    //Recorders recorders;


    @Builder
    public TrainerOneStepACDC(EnvironmentI<V> environment,
                              AgentACDiscoI<V> agent,
                              SafetyLayerI<V> safetyLayer,
                              TrainerParameters trainerParameters,
                              StateI<V> startState) {
        this.environment = environment;
        this.agent = agent;
        this.trainerParameters = trainerParameters;
        this.experienceCreator= ExperienceCreator.<V>builder()
                .environment(environment).safetyLayer(safetyLayer).parameters(trainerParameters)
                .build();
        this.startState=startState;
    }

    public void train() {
        var episodeTrainer = ACDCOneStepEpisodeTrainer
                .<V>builder()
                .agent(agent).parameters(trainerParameters)
                .build();

        for (int ei = 0; ei < trainerParameters.nofEpisodes(); ei++) {
            agent.setState(startState.copy());
            var experiences = experienceCreator.getExperiences(agent);
            episodeTrainer.trainAgentFromExperiences(experiences);

            experiences.forEach(System.out::println);

        }
    }


    //updateRecorders((s) -> agent.getHelper().calcActionProbsInObsState(s),lossActorAndCritic);



}
