package policy_gradient_problems.the_problems.short_corridor;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.abstract_classes.AgentParamActorI;
import policy_gradient_problems.common_trainers.ParamActorEpisodeTrainer;
import policy_gradient_problems.common_value_classes.TrainerParameters;


@Log
public class TrainerVanillaSC extends TrainerAbstractSC {

    AgentParamActorSC agent;

    @Builder
    public TrainerVanillaSC(@NonNull EnvironmentSC environment,
                            @NonNull AgentParamActorSC agent,
                            @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent=agent;
    }


    public void train() {
        var episodeTrainer= new ParamActorEpisodeTrainer<>(agent,parameters);
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setStateAsRandomNonTerminal();
            agent.setState(StateSC.randomNonTerminal());
            episodeTrainer.trainFromEpisode(getExperiences(agent));
            updateTracker(ei,agent.getHelper());
        }
    }


}
