package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.agent.AgentI;
import book_rl_explained.lunar_lander.domain.environment.EnvironmentI;
import book_rl_explained.lunar_lander.domain.environment.StartStateSupplierI;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import lombok.Builder;
import lombok.With;

/**
 * Represents the dependencies required for a trainer in the lunar lander domain.
 * This includes the agent, environment, trainer parameters, and start
 * state supplier.
 */

@Builder
@With
public record TrainerDependencies(
        AgentI agent,
        EnvironmentI environment,
        TrainerParameters trainerParameters,
        StartStateSupplierI startStateSupplier
) {

    public StateLunar startState() {
        return startStateSupplier.getStartState();
    }

    public double getGamma() {
        return trainerParameters().gamma();
    }

    public int getNofEpisodes() {
        return trainerParameters().nEpisodes();
    }

}
