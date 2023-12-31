package policy_gradient_problems.the_problems.cart_pole;


import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.agent_interfaces.AgentParamActorI;
import policy_gradient_problems.common_episode_trainers.ParamActorEpisodeTrainer;
import policy_gradient_problems.common_value_classes.TrainerParameters;

public class TrainerVanillaPole extends TrainerAbstractPole {

    AgentParamActorI<VariablesPole> agent;

    @Builder
    public TrainerVanillaPole(@NonNull EnvironmentPole environment,
                            @NonNull AgentParamActorPole agent,
                            @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent=agent;
    }

    public void train() {
        var episodeTrainer= new ParamActorEpisodeTrainer<>(agent,parameters);
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StatePole.newAngleAndPosRandom(environment.getParameters()));
            var experiences = getExperiences(agent);
            episodeTrainer.trainFromEpisode(experiences);
            updateTracker(ei, experiences);
        }
    }

}
