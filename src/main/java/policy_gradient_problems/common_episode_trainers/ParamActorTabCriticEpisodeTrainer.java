package policy_gradient_problems.common_episode_trainers;

import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.agent_interfaces.AgentParamActorTabCriticI;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_helpers.ReturnCalculator;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import java.util.List;
import java.util.function.Function;

@Builder
public class ParamActorTabCriticEpisodeTrainer<V> {
    @NonNull AgentParamActorTabCriticI<V> agent;
    @NonNull TrainerParameters parameters;
    @NonNull Double valueTermState;
    @NonNull Function<V, Integer> tabularCoder;  //transforms state to key used by critic function
    @NonNull Function<StateI<V>, Boolean> isTerminal;

    public void trainAgentFromExperiences(List<Experience<V>> experienceList) {
        double I = 1;
        var rc = new ReturnCalculator<V>();
        var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
        for (Experience<V> experience : elwr) {
            var gradLogVector = agent.calcGradLogVector(experience.state(), experience.action());
            double tdError = calcTdError(experience);
            int key = getTabularFunctionKey(experience.state());
            double changeTd = parameters.learningRateActor() * I * tdError;
            agent.changeCritic(key, changeTd);
            var change = gradLogVector.mapMultiplyToSelf(changeTd);
            agent.changeActor(change);
            I = I * parameters.gamma();
        }
    }

    private int getTabularFunctionKey(StateI<V> state) {
        return tabularCoder.apply(state.getVariables());
    }

    private double calcTdError(Experience<V> experience) {
        int keyState = getTabularFunctionKey(experience.state());
        int keyStateNext = getTabularFunctionKey(experience.stateNext());
        double v = agent.getCriticValue(keyState);
        double vNext = isTerminal.apply(experience.stateNext())
                ? valueTermState
                : agent.getCriticValue(keyStateNext);
        return experience.reward() + parameters.gamma() * vNext - v;
    }

}
