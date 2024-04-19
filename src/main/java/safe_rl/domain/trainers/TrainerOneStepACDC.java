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
import safe_rl.environments.buying_electricity.AgentACDCSafeBuyer;
import safe_rl.helpers.EpisodeInfo;

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
            setStartState();

          //  System.out.println("ei = " + ei);

            var experiences = getExperiences();
            episodeTrainer.trainAgentFromExperiences(experiences);


            //var eexpInfo=new EpisodeInfo<>(experiences);
            //experiences.forEach(System.out::println);

        }
        AgentACDCSafeBuyer ac=(AgentACDCSafeBuyer) agent;
        System.out.println("ac.getCritic() = " + ac.getCritic());
        System.out.println("ac.getActorMean() = " + ac.getActorMean());
    }

    public List<Experience<V>> evaluate() {
        setStartState();
        return getExperiences();
    }


    void setStartState() {
        agent.setState(startState.copy());
    }

    List<Experience<V>> getExperiences() {
        return experienceCreator.getExperiences(agent);
    }


    //updateRecorders((s) -> agent.getHelper().calcActionProbsInObsState(s),lossActorAndCritic);



}
