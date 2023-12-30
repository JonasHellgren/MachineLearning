package policy_gradient_problems.the_problems.short_corridor;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.ReturnCalculator;
import policy_gradient_problems.common_value_classes.ExperienceOld;
import policy_gradient_problems.common.TabularValueFunction;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.common.ReturnCalculatorOld;

import java.util.List;

/**
 * Explained in shortCorridor.md
 */

@Getter
public class TrainerActorCriticSC extends TrainerAbstractSC {

    public static final double VALUE_TERMINAL_STATE = 0;

    @Builder
    public TrainerActorCriticSC(@NonNull EnvironmentSC environment,
                                @NonNull AgentSC agent,
                                @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters);
    }

    public void train() {
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setStateAsRandomNonTerminal();
            trainAgentFromExperiences(getExperiences(agent));
            updateTracker(ei);
        }
    }

    private void trainAgentFromExperiences(List<Experience<VariablesSC>> experienceList) {
        double I = 1;
        var returnCalculator = new ReturnCalculator<VariablesSC>();
        var expListWithReturns = returnCalculator.createExperienceListWithReturns(experienceList, parameters.gamma());
        for (Experience<VariablesSC> experience : expListWithReturns) {
            var gradLogVector = agent.calcGradLogVector(experience.state(), experience.action());
            double delta = calcDelta(experience);
            getCriticParams().updateFromExperience(EnvironmentSC.getPos(experience.state()), I * delta, parameters.learningRateCritic());
            var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateActor() * I * delta);
            agent.setActorParams(agent.getActorParams().add(changeInThetaVector));
            I = I * parameters.gamma();
        }
    }

    private TabularValueFunction getCriticParams() {
        return agent.getCriticParams();
    }

    private double calcDelta(Experience<VariablesSC> experience) {
        double v = getCriticParams().getValue(EnvironmentSC.getPos(experience.state()));
        double vNext = environment.isTerminalObserved(EnvironmentSC.getPos(experience.stateNext()))
                ? VALUE_TERMINAL_STATE
                : getCriticParams().getValue(EnvironmentSC.getPos(experience.stateNext()));
        return experience.reward() + parameters.gamma() * vNext - v;
    }


}
