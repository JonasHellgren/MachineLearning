package policy_gradient_problems.domain.common_episode_trainers;

import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.domain.agent_interfaces.AgentParamActorTabCriticI;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.helpers.ReturnCalculator;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import java.util.List;
import java.util.function.Function;

@Builder
public class ParamActorTabCriticEpisodeTrainer<V> implements EpisodeTrainerI<V> {
    @NonNull AgentParamActorTabCriticI<V> agent;
    @NonNull TrainerParameters parameters;
    @NonNull Double valueTermState;
    @NonNull Function<V, Integer> tabularCoder;  //transforms state to key used by critic function
    @NonNull Function<StateI<V>, Boolean> isTerminal;

    @Override
    public void trainAgentFromExperiences(List<Experience<V>> experienceList) {
        var rc = new ReturnCalculator<V>();
        var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
        for (Experience<V> experience : elwr) {
            var gradLogVector = agent.calcGradLogVector(experience.state(), experience.action());
            double tdError = calcTdError(experience);
            int key = getTabularFunctionKey(experience.state());
            double changeTd = parameters.learningRateNonNeuralActor() * tdError;
            agent.changeCritic(key, changeTd);
            var change = gradLogVector.mapMultiplyToSelf(changeTd);
            agent.changeActor(change);
        }
    }

    private int getTabularFunctionKey(StateI<V> state) {
        return tabularCoder.apply(state.getVariables());
    }

    private double calcTdError(Experience<V> experience) {
        int keyState = getTabularFunctionKey(experience.state());
        int keyStateNext = getTabularFunctionKey(experience.stateNext());
        double v = agent.getCriticValue(keyState);
        double vNext = Boolean.TRUE.equals(isTerminal.apply(experience.stateNext()))
                ? valueTermState
                : agent.getCriticValue(keyStateNext);
        return experience.reward() + parameters.gamma() * vNext - v;
    }

}
