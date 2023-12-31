package policy_gradient_problems.common_episode_trainers;

import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.abstract_classes.AgentParamActorTabCriticI;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.ReturnCalculator;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import java.util.List;
import java.util.function.Function;

@Builder
public class ParamActorTabBaselineEpisodeTrainer<V> {
    @NonNull AgentParamActorTabCriticI<V> agent;
    @NonNull TrainerParameters parameters;
    @NonNull Function<V, Integer> tabularCoder;  //transforms state to key used by critic function

    public void trainAgentFromExperiences(List<Experience<V>> experienceList) {
        var returnCalculator=new ReturnCalculator<V>();
        var expListWithReturns = returnCalculator.createExperienceListWithReturns(experienceList,parameters.gamma());
        for (Experience<V> experience:expListWithReturns) {
            var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
            double tdError = calcTdError(experience);
            int key = getTabularFunctionKey(experience.state());
            double changeTd = parameters.learningRateActor() * tdError;
            agent.changeCritic(key, changeTd);
            var changeInThetaVector = gradLogVector.mapMultiplyToSelf(changeTd);
            agent.changeActor(changeInThetaVector);
        }
    }

    private int getTabularFunctionKey(StateI<V> state) {
        return tabularCoder.apply(state.getVariables());
    }

    private double calcTdError(Experience<V> experience) {
        int key = getTabularFunctionKey(experience.state());
        double value= agent.getCriticValue(key);
        double Gt=experience.value();
        return Gt-value;
    }


}
