package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.agent.AgentI;
import book_rl_explained.lunar_lander.domain.environment.EnvironmentI;
import book_rl_explained.lunar_lander.domain.environment.StartStateSupplierI;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import lombok.Builder;

@Builder
public record TrainerDependencies(
        AgentI agent,
        EnvironmentI environment,
        TrainerParameters trainerParameters,
        StartStateSupplierI startStateSupplier
) {

    public StateLunar startState() {
        return startStateSupplier.getStartState();
    }
}
