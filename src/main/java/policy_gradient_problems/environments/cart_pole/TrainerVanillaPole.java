package policy_gradient_problems.environments.cart_pole;


import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.domain.agent_interfaces.AgentParamActorI;
import policy_gradient_problems.domain.common_episode_trainers.ParamActorEpisodeTrainer;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

public final class TrainerVanillaPole extends TrainerAbstractPole {

    AgentParamActorI<VariablesPole> agent;

    @Builder
    public TrainerVanillaPole(@NonNull EnvironmentPole environment,
                            @NonNull AgentParamActorPole agent,
                            @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent=agent;
    }

    @Override
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
