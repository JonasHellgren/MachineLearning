package policy_gradient_problems.the_problems.short_corridor;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.ReturnCalculator;
import policy_gradient_problems.common_trainers.ParamActorTrainer;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.twoArmedBandit.VariablesBandit;


@Log
public class TrainerVanillaSC extends TrainerAbstractSC {

    @Builder
    public TrainerVanillaSC(@NonNull EnvironmentSC environment,
                            @NonNull AgentSC agent,
                            @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters);
    }



    public void train() {
        ParamActorTrainer<VariablesSC> episodeTrainer= new ParamActorTrainer<>(agent,parameters);
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setStateAsRandomNonTerminal();
            episodeTrainer.trainFromEpisode(getExperiences(agent));
            updateTracker(ei);
        }
    }


}
