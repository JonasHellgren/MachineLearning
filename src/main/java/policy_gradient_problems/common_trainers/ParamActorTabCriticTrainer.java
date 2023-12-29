package policy_gradient_problems.common_trainers;

import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import policy_gradient_problems.abstract_classes.AgentParamActorI;
import policy_gradient_problems.common.TabularValueFunction;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.ReturnCalculator;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import java.util.List;
import java.util.function.Function;

@Builder
@Setter
public class ParamActorTabCriticTrainer<V> {
    @NonNull AgentParamActorI<V> agent;
    @NonNull TrainerParameters parameters;
    @NonNull Double valueTermState;
    @NonNull Function<V,Integer> tabularCoder;  //transforms state to key used by critic function

    public void trainAgentFromExperiences(List<Experience<V>> experienceList) {
        double I=1;
        var rc=new ReturnCalculator<V>();
        var elwr=rc.createExperienceListWithReturns(experienceList,parameters.gamma());
        for (Experience<V> experience: elwr) {
            var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
            double delta = calcDelta(experience);
            int tc = getTabularCoder(experience);
            getCriticParams().updateFromExperience(tc, I*delta, parameters.learningRateCritic());
            var change = gradLogVector.mapMultiplyToSelf(parameters.learningRateActor()* I *delta);
            agent.changeActor(change);
            I = I *parameters.gamma();
        }
    }

    private TabularValueFunction getCriticParams() {
        return agent.getCriticParams();
    }

    private int getTabularCoder(Experience<V> experience) {
        return tabularCoder.apply(experience.state().getVariables());
    }

    private double calcDelta(Experience<V> experience) {
        double v= getCriticParams().getValue(getTabularCoder(experience));
        return experience.reward()+parameters.gamma()*valueTermState-v;
    }

}
