package policy_gradient_problems.the_problems.sink_the_ship;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import policy_gradient_problems.common.*;
import policy_gradient_problems.common.ReturnCalculatorOld;
import policy_gradient_problems.common_value_classes.ExperienceContAction;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.List;

@Getter
public class TrainerActorCriticShip extends TrainerAbstractShip  {

    public static final double VALUE_TERMINAL_STATE = 0;
    TabularValueFunction valueFunction;

    @Builder
    public TrainerActorCriticShip(@NonNull EnvironmentShip environment,
                                @NonNull AgentShip agent,
                                @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters);
        valueFunction=new TabularValueFunction(EnvironmentShip.STATES.size());
    }

    public void train() {
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setRandomState();
            trainAgentFromExperiences(getExperiences());
            updateTracker(ei,valueFunction);
        }
    }

    private void trainAgentFromExperiences(List<ExperienceContAction> experienceList) {
        double I=1;
        var returnCalculator=new ReturnCalculatorOld();
        var expListWithReturns=returnCalculator.createExperienceListWithReturnsContActions(experienceList,parameters.gamma());

        for (ExperienceContAction experience: expListWithReturns) {
            var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
            double delta = calcDelta(experience);
            valueFunction.updateFromExperienceCont(experience, I*delta, parameters.learningRateCritic());
            var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateActor()* I *delta);
            agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
            I = I *parameters.gamma();
        }
    }

    private double calcDelta(ExperienceContAction experience) {
        double v=valueFunction.getValue(experience.state());
        return experience.reward()+parameters.gamma()*VALUE_TERMINAL_STATE -v;
    }

}
