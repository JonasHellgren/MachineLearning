package policy_gradient_problems.the_problems.short_corridor;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import policy_gradient_problems.common_episode_trainers.ParamActorTabBaselineEpisodeTrainer;
import policy_gradient_problems.common_value_classes.TrainerParameters;

/**
 * Explained in shortCorridor.md
 */

@Getter
public final class TrainerBaselineSC extends TrainerAbstractSC {

    AgentParamActorTabCriticSC agent;

    @Builder
    public TrainerBaselineSC(@NonNull EnvironmentSC environment,
                             @NonNull AgentParamActorTabCriticSC agent,
                             @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent=agent;
    }

    @Override
    public void train() {
        ParamActorTabBaselineEpisodeTrainer<VariablesSC> episodeTrainer = ParamActorTabBaselineEpisodeTrainer
                .<VariablesSC>builder()
                .agent(agent).parameters(parameters)
                .tabularCoder((v) -> v.pos())
                .build();

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StateSC.randomNonTerminal());
            episodeTrainer.trainAgentFromExperiences(getExperiences(agent));
            updateTracker(ei,agent.getHelper());
        }
    }
}
