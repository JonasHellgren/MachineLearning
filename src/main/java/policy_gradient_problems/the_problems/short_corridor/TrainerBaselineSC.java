package policy_gradient_problems.the_problems.short_corridor;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import policy_gradient_problems.common_value_classes.ExperienceDiscreteAction;
import policy_gradient_problems.common.TabularValueFunction;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.common.ReturnCalculator;

import java.util.List;

/**
 * Explained in shortCorridor.md
 */

@Getter
public class TrainerBaselineSC extends TrainerAbstractSC {

    TabularValueFunction valueFunction;

    @Builder
    public TrainerBaselineSC(@NonNull EnvironmentSC environment,
                             @NonNull AgentSC agent,
                             @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters);
        valueFunction=new TabularValueFunction(EnvironmentSC.SET_OBSERVABLE_STATES_NON_TERMINAL.size());
    }

    public void train() {
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setStateAsRandomNonTerminal();
            trainAgentFromExperiences(getExperiences());
            updateTracker(ei);
        }
    }

    private void trainAgentFromExperiences(List<ExperienceDiscreteAction> experienceList) {
        var returnCalculator=new ReturnCalculator();
        var expListWithReturns = returnCalculator.createExperienceListWithReturns(experienceList,parameters.gamma());
        for (ExperienceDiscreteAction experience:expListWithReturns) {
            var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
            double delta = calcDelta(experience);
            valueFunction.updateFromExperience(experience, delta, parameters.beta());
            var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRate() * delta);
            agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
        }
    }

    private double calcDelta(ExperienceDiscreteAction experience) {
        double value= valueFunction.getValue(experience.state());
        double Gt=experience.value();
        return Gt-value;
    }



}
