package policy_gradient_problems.common_trainers;

import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.abstract_classes.AgentParamActorI;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.common.TabularValueFunction;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.ReturnCalculator;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.List;
import java.util.function.Function;

@Builder
public class ParamActorTabCriticTrainer<V> {
    @NonNull AgentParamActorI<V> agent;
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
            double delta = calcDelta(experience);
            int key = getTabularFunctionKey(experience.state());
            getCriticParams().updateFromExperience(key, I * delta, parameters.learningRateCritic());
            var change = gradLogVector.mapMultiplyToSelf(parameters.learningRateActor() * I * delta);
            agent.changeActor(change);
            I = I * parameters.gamma();
        }
    }

    private TabularValueFunction getCriticParams() {
        return agent.getCritic();
    }

    private int getTabularFunctionKey(StateI<V> state) {
        return tabularCoder.apply(state.getVariables());
    }

    private double calcDelta(Experience<V> experience) {
        int keyState = getTabularFunctionKey(experience.state());
        int keyStateNext = getTabularFunctionKey(experience.stateNext());

        double v = getCriticParams().getValue(keyState);
        double vNext = isTerminal.apply(experience.stateNext())
                ? valueTermState
                : getCriticParams().getValue(keyStateNext);
        return experience.reward() + parameters.gamma() * vNext - v;
    }

}
