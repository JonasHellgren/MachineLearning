package policy_gradient_problems.the_problems.short_corridor;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import policy_gradient_problems.common_value_classes.ExperienceOld;
import policy_gradient_problems.common.TabularValueFunction;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.common.ReturnCalculatorOld;

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

    private void trainAgentFromExperiences(List<ExperienceOld> experienceList) {
        var returnCalculator=new ReturnCalculatorOld();
        var expListWithReturns = returnCalculator.createExperienceListWithReturns(experienceList,parameters.gamma());
        for (ExperienceOld experience:expListWithReturns) {
            var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
            double delta = calcDelta(experience);
            valueFunction.updateFromExperience(experience, delta, parameters.learningRateCritic());
            var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateActor() * delta);
            agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
        }
    }

    private double calcDelta(ExperienceOld experience) {
        double value= valueFunction.getValue(experience.state());
        double Gt=experience.value();
        return Gt-value;
    }



}
