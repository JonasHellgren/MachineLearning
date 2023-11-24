package policy_gradient_problems.short_corridor;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import policy_gradient_problems.common.Experience;
import policy_gradient_problems.common.TabularValueFunction;
import policy_gradient_problems.common.TrainerParameters;
import policy_gradient_problems.common.TrainingTracker;
import policy_gradient_problems.helpers.ReturnCalculator;

import java.util.List;

@Getter
public class TrainerActorCriticSC extends TrainerAbstractSC {

    public static final double VALUE_TERMINAL_STATE = 0;
    TabularValueFunction valueFunction;

    @Builder
    public TrainerActorCriticSC(@NonNull EnvironmentSC environment,
                             @NonNull AgentSC agent,
                             @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters, new TrainingTracker());
        valueFunction=new TabularValueFunction(EnvironmentSC.SET_OBSERVABLE_STATES_NON_TERMINAL.size());
    }

    public void train() {
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setStateAsRandomNonTerminal();
            trainAgentFromExperiences(getExperiences());
            updateTracker(ei);
        }
    }

    private void trainAgentFromExperiences(List<Experience> experienceList) {
        double I=1;
        var returnCalculator=new ReturnCalculator();
        var expListWithReturns=returnCalculator.createExperienceListWithReturns(experienceList,parameters.gamma());
        for (Experience experience: expListWithReturns) {
            var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
            double delta = calcDelta(experience);
            valueFunction.updateFromExperience(experience, I*delta, parameters.beta());
            var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRate()* I *delta);
            agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
            I = I *parameters.gamma();
        }
    }

    private double calcDelta(Experience experience) {
        double v=valueFunction.getValue(experience.state());
        double vNext= environment.isTerminalObserved(experience.stateNext())
                ? VALUE_TERMINAL_STATE
                : valueFunction.getValue(experience.stateNext());
        return experience.reward()+parameters.gamma()*vNext-v;
    }


}
