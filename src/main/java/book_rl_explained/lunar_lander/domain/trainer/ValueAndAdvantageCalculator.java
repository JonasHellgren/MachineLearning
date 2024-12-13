package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.agent.AgentI;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;


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

    public  double valueOfTakingAction(@NonNull Boolean isStateOutside,
                                       StateLunar stateFuture,
                                       double sumRewards) {
        var agent=dependencies.agent();
        var parameters=dependencies.trainerParameters();
        return isStateOutside
                ? sumRewards
                : sumRewards + parameters.gammaPowN() * agent.readCritic(stateFuture);
    }


    public  double advantage(@NotNull AgentI agent, StateLunar state, double valueOfTakingAction) {
        return valueOfTakingAction - agent.readCritic(state);
    }


}
