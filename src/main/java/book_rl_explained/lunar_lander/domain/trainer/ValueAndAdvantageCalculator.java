package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.agent.AgentI;
import book_rl_explained.lunar_lander.domain.agent.AgentI;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import lombok.AllArgsConstructor;
import lombok.NonNull;


/**
 * Calculator for multi-step result items.
 * This class provides methods for calculating loss and advantage values
 * for multi-step result items.
 *
 *  Methods valueOfTakingAction and advantage are explained advantage_actionvalue.md
 *
 *  The loss functions are used by FitterUsingReplayBuffer to update agent memory
 *
 **/

@AllArgsConstructor
public class ValueAndAdvantageCalculator {

    TrainerDependencies dependencies;

    public static ValueAndAdvantageCalculator of(TrainerDependencies dependencies) {
        return new ValueAndAdvantageCalculator(dependencies);
    }

    public  double valueOfTakingAction(@NonNull Boolean stateFutureTerminalOrNotPresent,
                                       StateLunar stateFuture,
                                       double sumRewards) {
        var agent=dependencies.agent();
        var parameters=dependencies.trainerParameters();
        return stateFutureTerminalOrNotPresent
                ? sumRewards
                : sumRewards + parameters.gammaPowN() * agent.readCritic(stateFuture);
    }

    double advantage(AgentI agent,
                     ExperienceLunar singleStepExperience,
                     EvaluateResult rs) {
        return advantage(agent,
                rs.isFutureOutsideOrTerminal(),
                singleStepExperience.state(),
                rs.stateFuture(),
                rs.sumRewardsNSteps());
    }

    double advantage(@NonNull AgentI agent, MultiStepResultItem msri) {
        return advantage(agent,
                msri.isStateFutureTerminalOrNotPresent(),
                msri.state(),
                msri.stateFuture(),
                msri.sumRewards());
    }

    private double advantage(@NonNull AgentI agent,
                             @NonNull Boolean stateFutureTerminalOrNotPresent,
                             StateLunar state,
                             StateLunar stateFuture,
                             double sumRewards) {
        var value = valueOfTakingAction(stateFutureTerminalOrNotPresent, stateFuture, sumRewards);
        return value - agent.readCritic(state);
    }

}
