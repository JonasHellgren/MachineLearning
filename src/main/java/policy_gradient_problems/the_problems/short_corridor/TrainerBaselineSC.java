package policy_gradient_problems.the_problems.short_corridor;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.ReturnCalculator;
import policy_gradient_problems.common.TabularValueFunction;
import policy_gradient_problems.common_trainers.ParamActorTabBaselineTrainer;
import policy_gradient_problems.common_trainers.ParamActorTrainer;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.List;

/**
 * Explained in shortCorridor.md
 */

@Getter
public class TrainerBaselineSC extends TrainerAbstractSC {

    @Builder
    public TrainerBaselineSC(@NonNull EnvironmentSC environment,
                             @NonNull AgentSC agent,
                             @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters);
    }


    public void train() {
        ParamActorTabBaselineTrainer<VariablesSC> episodeTrainer = ParamActorTabBaselineTrainer
                .<VariablesSC>builder()
                .agent(agent).parameters(parameters)
                .tabularCoder((v) -> v.pos())
                .build();

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setStateAsRandomNonTerminal();
            episodeTrainer.trainAgentFromExperiences(getExperiences(agent));
            updateTracker(ei);
        }
    }
}
