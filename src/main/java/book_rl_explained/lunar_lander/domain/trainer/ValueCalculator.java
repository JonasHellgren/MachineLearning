package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.agent.AgentI;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * This class calculates the value and advantage of taking an action in a given state.
 * It also calculates the temporal difference error for a given experience.
 */

@AllArgsConstructor
public class ValueCalculator {
    public static final double VALUE_TERM = 0d;

    TrainerDependencies dependencies;

    public static ValueCalculator of(TrainerDependencies dependencies) {
        return new ValueCalculator(dependencies);
    }

    /**
     * Calculates the value of taking an action in a given state.
     *
     * @param isStateOutside whether the state is outside the environment
     * @param stateFuture the future state of the environment
     * @param sumRewards the sum of rewards received so far
     * @return the value of taking an action in the given state
     */
    public  double valueOfTakingAction(@NonNull Boolean isStateOutside,
                                       StateLunar stateFuture,
                                       double sumRewards) {
        var agent=dependencies.agent();
        var parameters=dependencies.trainerParameters();
        return isStateOutside
                ? sumRewards
                : sumRewards + parameters.gammaPowN() * agent.readCritic(stateFuture);
    }

    /**
     * Calculates the advantage of taking an action in a given state.
     *
     * @param agent the agent taking the action
     * @param state the current state of the environment
     * @param value the value of taking an action in the given state
     * @return the advantage of taking an action in the given state
     */
    public  double advantage(@NotNull AgentI agent, StateLunar state, double value) {
        double valueCritic = agent.readCritic(state);
        return value - valueCritic;
    }

    /**
     * Calculates the temporal difference error for a given experience.
     *
     * @param experience the experience to calculate the error for
     * @return the temporal difference error for the given experience
     */
    public double temporalDifferenceError(ExperienceLunar experience) {
        var agent = dependencies.agent();
        double valueCritic = agent.readCritic(experience.state());
        double vNext = experience.isTransitionToTerminal()
                ? VALUE_TERM
                : agent.readCritic(experience.stateNew());
        double value = experience.reward() + dependencies.getGamma() * vNext;
        return value - valueCritic;
    }


}
