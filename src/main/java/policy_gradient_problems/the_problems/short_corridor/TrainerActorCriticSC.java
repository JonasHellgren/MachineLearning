package policy_gradient_problems.the_problems.short_corridor;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.ReturnCalculator;
import policy_gradient_problems.common.TabularValueFunction;
import policy_gradient_problems.common_trainers.ParamActorTabCriticTrainer;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.sink_the_ship.VariablesShip;

import java.util.List;

/**
 * Explained in shortCorridor.md
 */

@Getter
public class TrainerActorCriticSC extends TrainerAbstractSC {

    public static final double VALUE_TERMINAL_STATE = 0;

    @Builder
    public TrainerActorCriticSC(@NonNull EnvironmentSC environment,
                                @NonNull AgentSC agent,
                                @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters);
    }


    public void train() {
        ParamActorTabCriticTrainer<VariablesSC> episodeTrainer =
                ParamActorTabCriticTrainer.<VariablesSC>builder()
                        .agent(agent)
                        .parameters(parameters)
                        .valueTermState(VALUE_TERMINAL_STATE)
                        .tabularCoder((v) -> v.pos())
                        .isTerminal((s) -> environment.isTerminalObserved(EnvironmentSC.getPos(s)))
                        .build();

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setStateAsRandomNonTerminal();
            episodeTrainer.trainAgentFromExperiences(getExperiences(agent));
            updateTracker(ei);
        }
    }

}
